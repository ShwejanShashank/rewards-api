package com.example.rewards_api.service;

import com.example.rewards_api.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RewardServiceTest {

    private final RewardService rewardService = new RewardService();

    @Test
    void testRewardCalculationWithinThreeMonths() {
        List<Transaction> transactions = List.of(
                new Transaction("cust1", LocalDate.now().minusMonths(1), 120), // 90
                new Transaction("cust1", LocalDate.now().minusMonths(2), 90),  // 40
                new Transaction("cust2", LocalDate.now().minusMonths(3), 45)   // 0
        );

        var result = rewardService.calculateRewards(transactions);
        assertEquals(2, result.size());
        assertEquals(130, result.stream().filter(r -> r.getCustomerId().equals("cust1")).findFirst().get().getTotalPoints());
        assertEquals(0, result.stream().filter(r -> r.getCustomerId().equals("cust2")).findFirst().get().getTotalPoints());
    }

    @Test
    void testIgnoreOldTransactions() {
        List<Transaction> transactions = List.of(
                new Transaction("cust1", LocalDate.now().minusMonths(5), 200) // Should be ignored
        );

        var result = rewardService.calculateRewards(transactions);
        assertTrue(result.isEmpty());
    }
}
