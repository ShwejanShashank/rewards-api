package com.example.rewards_api.repository;

import com.example.rewards_api.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Simulates a repository for storing customer transactions in memory using ConcurrentHashMap.
 */
@Component
public class TransactionRepository {
    private final ConcurrentHashMap<String, List<Transaction>> store = new ConcurrentHashMap<>();

    /**
     * Save transactions for a specific customer.
     */
    public void save(String customerId, List<Transaction> transactions) {
        store.put(customerId, transactions);
    }
    /**
     * Find transactions for a specific customer.
     */
    public List<Transaction> findByCustomerId(String customerId) {
        return store.getOrDefault(customerId, List.of());
    }

    /**
     * checking whether  customer exists
     */
    public boolean exists(String customerId) {
        return store.containsKey(customerId);
    }
    /**
     * Get all stored transactions.
     */
    public ConcurrentHashMap<String, List<Transaction>> findAll() {
        return store;
    }
}
