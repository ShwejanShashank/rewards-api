package com.example.rewards_api.service;

import com.example.rewards_api.model.dto.CustomerRewardResponse;
import com.example.rewards_api.model.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Interface defining reward-related operations.
 */
public interface RewardService {

    /**
     * Calculates rewards for all customers from a list of transactions.
     */
    List<CustomerRewardResponse> calculateRewards(List<Transaction> transactions);

    /**
     * Get rewards for a single customer by ID.
     */

    CustomerRewardResponse getRewardsByCustomer(String customerId);


    /**
     * Get all transactions for a single customer.
     */

    List<Transaction> getTransactionsForCustomer(String customerId);

    void addCustomer(String customerId);

    void addTransactions(List<Transaction> transaction);

    Map<String, Integer> getAllCustomerTotalPoints();
}
