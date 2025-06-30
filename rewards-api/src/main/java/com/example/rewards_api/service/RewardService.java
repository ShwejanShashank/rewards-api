package com.example.rewards_api.service;

import com.example.rewards_api.dto.CustomerRewardResponse;
import com.example.rewards_api.dto.MonthlyReward;
import com.example.rewards_api.model.Transaction;
import com.example.rewards_api.util.RewardCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Service layer to calculate customer reward points from transactions.
 */
@Service
public class RewardService {
    /**
     * Calculates rewards for all customers from the transaction list.
     * Filters transactions from the last 3 months only.
     *
     * @param transactions List of customer transactions
     * @return List of customer reward responses
     */
    public List<CustomerRewardResponse> calculateRewards(List<Transaction> transactions) {
        Map<String, Map<String, Integer>> customerMonthlyPoints = new HashMap<>();

        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3).withDayOfMonth(1);

        for (Transaction tx : transactions) {
            if (tx.getDate().isBefore(threeMonthsAgo)) continue;

            int points = RewardCalculator.calculatePoints(tx.getAmount());
            String customerId = tx.getCustomerId();
            String month = tx.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            customerMonthlyPoints
                    .computeIfAbsent(customerId, k -> new HashMap<>())
                    .merge(month, points, Integer::sum);
        }
        /**
         * Returns mock reward calculation for a single customer using sample data.
         *
         * @param customerId ID of the customer
         * @return Reward response with monthly and total points
         */
        return customerMonthlyPoints.entrySet().stream()
                .map(entry -> {
                    String customerId = entry.getKey();
                    List<MonthlyReward> monthlyRewards = entry.getValue().entrySet().stream()
                            .map(e -> new MonthlyReward(e.getKey(), e.getValue()))
                            .collect(Collectors.toList());
                    int total = monthlyRewards.stream().mapToInt(MonthlyReward::getPoints).sum();
                    return new CustomerRewardResponse(customerId, monthlyRewards, total);
                })
                .collect(Collectors.toList());
    }

    public CustomerRewardResponse getRewardsByCustomer(String customerId) {
        List<Transaction> mock = List.of(
                new Transaction(customerId, LocalDate.now().minusMonths(1), 120),
                new Transaction(customerId, LocalDate.now().minusMonths(2), 75)
        );
        return calculateRewards(mock).get(0);
    }
}