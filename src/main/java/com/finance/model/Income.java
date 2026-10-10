package com.finance.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * [OOP Rubric: Inheritance & Polymorphism]
 * Income extends Transaction, representing money earned (salary, freelancing).
 * Overrides getType() to return "INCOME".
 * ============================================================================
 */
public class Income extends Transaction {

    private String source;

    public Income() {
        super();
    }

    public Income(int id, int userId, String source, double amount, LocalDate date, String note, LocalDateTime createdAt) {
        super(id, userId, amount, date, note, createdAt);
        this.source = source;
    }

    @Override
    public String getType() {
        return "INCOME";
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Income{" +
                "id=" + getId() +
                ", amount=" + getAmount() +
                ", source='" + source + '\'' +
                ", date=" + getDate() +
                '}';
    }
}
