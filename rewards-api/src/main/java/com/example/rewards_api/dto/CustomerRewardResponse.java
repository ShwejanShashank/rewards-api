package com.example.rewards_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


/**
 * Aggregates all reward information for a customer including monthly breakdown and total.
 */


@Data
@AllArgsConstructor
public class CustomerRewardResponse {
    private String customerId;
    private List<MonthlyReward> monthlyRewards;
    private int totalPoints;
}
