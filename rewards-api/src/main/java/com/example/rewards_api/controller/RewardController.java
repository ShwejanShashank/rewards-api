package com.example.rewards_api.controller;

import com.example.rewards_api.dto.CustomerRewardResponse;
import com.example.rewards_api.model.Transaction;
import com.example.rewards_api.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
     * POST endpoint to calculate rewards for multiple customer transactions.
     *
     * @param transactions List of transactions
     * @return List of customer rewards
     */
    @PostMapping
    public ResponseEntity<List<CustomerRewardResponse>> calculateRewards(@RequestBody List<Transaction> transactions) {
        return ResponseEntity.ok(rewardService.calculateRewards(transactions));
    }
    /**
     * GET endpoint to retrieve rewards for a specific customer (mocked).
     *
     * @param customerId ID of the customer
     * @return Reward details
     */
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerRewardResponse> getCustomerRewards(@PathVariable String customerId) {
        return ResponseEntity.ok(rewardService.getRewardsByCustomer(customerId));
    }
}
