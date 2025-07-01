package com.example.rewards_api.controller;

import com.example.rewards_api.model.dto.CustomerRewardResponse;
import com.example.rewards_api.model.Transaction;
import com.example.rewards_api.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST Controller exposing endpoints for reward calculation.
 */
@RestController
@RequestMapping("/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Registers a new customer with an empty transaction list.
     *
     * @param customerId unique customer ID
     * @return success message
     */

    @PostMapping("/customers/{customerId}")
    public ResponseEntity<String> addCustomer(@PathVariable String customerId) {
        rewardService.addCustomer(customerId);
        return ResponseEntity.ok("Customer " + customerId + " added.");
    }

    /**
     * Adds transactions for customers that already exist.
     * Ignores transactions for unknown customer IDs.
     *
     * @param transactions list of new transactions
     * @return success message
     */
    @PostMapping("/transactions")
    public ResponseEntity<String> addTransactions(@RequestBody List<Transaction> transactions) {
        rewardService.addTransactions(transactions);
        return ResponseEntity.ok("Transactions processed.");
    }
    /**
     * Calculates reward points for provided customer IDs using their stored transactions.
     *
     * @param customerIds list of customer IDs
     * @return list of reward summaries per customer
     */
    @PostMapping
    public ResponseEntity<List<CustomerRewardResponse>> calculateRewards(@RequestBody List<String> customerIds) {
        return ResponseEntity.ok(
                rewardService.calculateRewards(
                        customerIds.stream()
                                .flatMap(id -> rewardService.getTransactionsForCustomer(id).stream())
                                .toList()
                )
        );
    }

    /**
     * Retrieves reward breakdown for a specific customer by ID.
     *
     * @param customerId the customer to query
     * @return reward summary (monthly + total)
     */

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerRewardResponse> getCustomerRewards(@PathVariable String customerId) {
        return ResponseEntity.ok(rewardService.getRewardsByCustomer(customerId));
    }
    /**
     * Returns a map of total accumulated points for all customers.
     *
     * @return map of customerId → totalPoints
     */
    @GetMapping("/totals")
    public ResponseEntity<Map<String, Integer>> getTotalPointsForAll() {
        return ResponseEntity.ok(rewardService.getAllCustomerTotalPoints());
    }
}