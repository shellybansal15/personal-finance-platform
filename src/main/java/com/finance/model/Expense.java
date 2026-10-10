package com.finance.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * [OOP Rubric: Inheritance & Polymorphism]
 * Expense extends Transaction, adding category metadata and overriding getType().
 * Represents money spent by a user.
 * ============================================================================
 */
public class Expense extends Transaction {

    private int categoryId;
    private String categoryName;

    public Expense() {
        super();
    }

    public Expense(int id, int userId, int categoryId, double amount, LocalDate date, String note, LocalDateTime createdAt) {
        super(id, userId, amount, date, note, createdAt);
        this.categoryId = categoryId;
    }

    @Override
    public String getType() {
        return "EXPENSE";
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + getId() +
                ", amount=" + getAmount() +
                ", category='" + categoryName + '\'' +
                ", date=" + getDate() +
                '}';
    }
}
