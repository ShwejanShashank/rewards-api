package com.example.rewards_api.service;

import com.example.rewards_api.model.Transaction;
import com.example.rewards_api.model.dto.CustomerRewardResponse;
import com.example.rewards_api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
/**
 * Unit tests for RewardService implementation.
 */
public class RewardServiceTest {

    private RewardService rewardService;

    @BeforeEach
    public void setup() {
        TransactionRepository repository = new TransactionRepository();

        repository.save("cust1", List.of(
                new Transaction("cust1", LocalDate.now().minusMonths(1), 120),  // 90 pts
                new Transaction("cust1", LocalDate.now().minusMonths(2), 80)    // 30 pts
        ));

        repository.save("cust2", List.of(
                new Transaction("cust2", LocalDate.now().minusMonths(1), 200),  // 250 pts
                new Transaction("cust2", LocalDate.now().minusMonths(4), 300)   // ignored
        ));

        rewardService = new com.example.rewards_api.service.RewardServiceImpl(repository);
    }
    /**
     * Verifies rewards are calculated correctly for existing customer transactions.
     */
    @Test
    public void testCalculateRewardsForExistingCustomer() {
        List<Transaction> transactions = rewardService.getTransactionsForCustomer("cust1");
        List<CustomerRewardResponse> rewards = rewardService.calculateRewards(transactions);

        assertEquals(1, rewards.size());
        CustomerRewardResponse response = rewards.get(0);
        assertEquals("cust1", response.getCustomerId());
        assertEquals(2, response.getMonthlyRewards().size());
        assertEquals(120, response.getTotalPoints());
    }
    /**
     * Ensures transactions older than 3 months are excluded.
     */

    @Test
    public void testCalculateRewardsWithOldTransactionExcluded() {
        List<Transaction> transactions = rewardService.getTransactionsForCustomer("cust2");
        List<CustomerRewardResponse> rewards = rewardService.calculateRewards(transactions);

        assertEquals(1, rewards.size());
        assertEquals("cust2", rewards.get(0).getCustomerId());
        assertEquals(250, rewards.get(0).getTotalPoints());  // only one transaction counted
    }

    /**
     * Verifies exception is thrown when requesting rewards for unknown customer.
     */

    @Test
    public void testGetRewardsByCustomerThrowsIfNotFound() {
        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            rewardService.getRewardsByCustomer("unknown");
        });
        assertTrue(ex.getMessage().contains("No rewards found"));
    }

    /**
     * Validates adding a customer and posting transactions reflects correct points.
     */

    @Test
    public void testAddCustomerAndTransactions() {
        rewardService.addCustomer("cust3");

        rewardService.addTransactions(List.of(
                new Transaction("cust3", LocalDate.now().minusMonths(1), 130),
                new Transaction("cust3", LocalDate.now().minusMonths(2), 60)
        ));

        CustomerRewardResponse response = rewardService.getRewardsByCustomer("cust3");
        assertEquals("cust3", response.getCustomerId());
        assertEquals(2, response.getMonthlyRewards().size());
        assertEquals(110 + 10, response.getTotalPoints());
    }
}