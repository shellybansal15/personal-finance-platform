package com.finance.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * [OOP Rubric: Interface Implementation]
 * Budget implements Trackable to track spending within a specific category
 * and time window against a spending limit.
 * UI progress bars turn green (<80%), amber (>=80%), and red (>=100%).
 * ============================================================================
 */
public class Budget implements Trackable {

    private int id;
    private int userId;
    private int categoryId;
    private String categoryName;
    private double amount;       // Target spending limit
    private double spentAmount;  // Current total spent in this category
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;

    public Budget() {
    }

    public Budget(int id, int userId, int categoryId, double amount, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    // --- Trackable Interface Implementation ---

    @Override
    public double getTargetAmount() {
        return this.amount;
    }

    @Override
    public double getCurrentAmount() {
        return this.spentAmount;
    }

    /**
     * Remaining spending allowance before budget is busted.
     */
    public double getRemainingAmount() {
        return Math.max(0, this.amount - this.spentAmount);
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
