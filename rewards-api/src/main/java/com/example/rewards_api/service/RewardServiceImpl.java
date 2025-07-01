package com.example.rewards_api.service;

import com.example.rewards_api.model.Transaction;
import com.example.rewards_api.model.dto.CustomerRewardResponse;
import com.example.rewards_api.model.dto.MonthlyReward;
import com.example.rewards_api.repository.TransactionRepository;
import com.example.rewards_api.service.RewardService;
import com.example.rewards_api.util.RewardCalculator;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {

    private final TransactionRepository repository;
    private final Map<String, Integer> customerTotalPoints = new ConcurrentHashMap<>();

    public RewardServiceImpl(TransactionRepository repository) {
        this.repository = repository;
    }
    /**
     * Seeds sample transactions at app startup for demo/testing purposes.
     */
    @PostConstruct
    public void loadSampleData() {
        addCustomer("cust1");
        addCustomer("cust2");
        addCustomer("cust3");

        repository.save("cust1", List.of(
                new Transaction("cust1", LocalDate.now().minusMonths(1), 120),
                new Transaction("cust1", LocalDate.now().minusMonths(2), 80)
        ));

        repository.save("cust2", List.of(
                new Transaction("cust2", LocalDate.now().minusMonths(1), 200),
                new Transaction("cust2", LocalDate.now().minusMonths(4), 300)
        ));

        repository.save("cust3", List.of(
                new Transaction("cust3", LocalDate.now().minusMonths(5), 130)
        ));
    }

    /**
     * Calculates reward points grouped by month and totaled per customer.
     * Filters transactions to only include the last 3 months.
     *
     * @param transactions list of all transactions
     * @return list of reward summaries
     */

    @Override
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

        List<CustomerRewardResponse> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, Integer>> entry : customerMonthlyPoints.entrySet()) {
            String customerId = entry.getKey();
            List<MonthlyReward> monthlyRewards = entry.getValue().entrySet().stream()
                    .map(e -> new MonthlyReward(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            int total = monthlyRewards.stream().mapToInt(MonthlyReward::getPoints).sum();

            customerTotalPoints.put(customerId, customerTotalPoints.getOrDefault(customerId, 0) + total);
            result.add(new CustomerRewardResponse(customerId, monthlyRewards, total));
        }

        return result;
    }

    /**
     * Returns rewards for a single customer, throws if no valid rewards found.
     *
     * @param customerId target customer
     * @return reward summary
     */

    @Override
    public CustomerRewardResponse getRewardsByCustomer(String customerId) {
        List<Transaction> transactions = repository.findByCustomerId(customerId);
        List<CustomerRewardResponse> rewards = calculateRewards(transactions);
        if (rewards.isEmpty()) throw new NoSuchElementException("No rewards found for customerId: " + customerId);
        return rewards.get(0);
    }
    /**
     * Fetches all transactions for a specific customer.
     *
     * @param customerId customer to fetch
     * @return list of transactions
     */

    @Override
    public List<Transaction> getTransactionsForCustomer(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    /**
     * Adds a new customer to the system with no transactions.
     *
     * @param customerId new customer ID
     */
    @Override
    public void addCustomer(String customerId) {
        repository.save(customerId, new ArrayList<>());
    }
    /**
     * Adds transactions to customers if they already exist.
     * Runs in parallel to optimize large batch loads.
     *
     * @param transactions incoming transactions
     */
    @Override
    public void addTransactions(List<Transaction> transactions) {
        transactions.parallelStream().forEach(tx -> {
            if (repository.exists(tx.getCustomerId())) {
                synchronized (repository) {
                    List<Transaction> existing = repository.findByCustomerId(tx.getCustomerId());
                    List<Transaction> updated = new ArrayList<>(existing);
                    updated.add(tx);
                    repository.save(tx.getCustomerId(), updated);
                }
            }
        });
    }
    /**
     * Retrieves a map of all customer IDs and their total accumulated reward points.
     *
     * @return map of customerId → totalPoints
     */
    @Override
    public Map<String, Integer> getAllCustomerTotalPoints() {
        return customerTotalPoints;
    }
}
