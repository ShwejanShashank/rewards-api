package com.example.rewards_api.util;


/**
 * Utility class to calculate reward points based on transaction amount.
 */
public class RewardCalculator {

    /**
     * Calculates reward points for a given transaction amount.
     * Rules:
     *  - 2 points for every dollar spent over $100
     *  - 1 point for every dollar spent over $50 up to $100
     *
     * @param amount Transaction amount
     * @return Reward points
     */

    public static int calculatePoints(double amount) {
        if (amount <= 50) return 0;
        if (amount <= 100) return (int) (amount - 50);
        return 2 * ((int) amount - 100) + 50;
    }
}
