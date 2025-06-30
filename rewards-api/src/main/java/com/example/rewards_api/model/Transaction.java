package com.example.rewards_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


/**
 * Represents a customer transaction with ID, date and amount spent.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String customerId;
    private LocalDate date;
    private double amount;
}
