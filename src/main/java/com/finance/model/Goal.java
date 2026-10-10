package com.finance.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * [OOP Rubric: Interface Implementation]
 * Goal implements Trackable to monitor long-term financial targets
 * (e.g., Emergency Fund, Vacation, New Laptop) with target amounts and deadlines.
 * ============================================================================
 */
public class Goal implements Trackable {

    private int id;
    private int userId;
    private String description;
    private double targetAmount;
    private double savedAmount;
    private LocalDate deadline;
    private String status; // 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'
    private LocalDateTime createdAt;

    public Goal() {
    }

    public Goal(int id, int userId, String description, double targetAmount, double savedAmount, LocalDate deadline, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.deadline = deadline;
        this.status = status;
        this.createdAt = createdAt;
    }

    // --- Trackable Interface Implementation ---

    @Override
    public double getTargetAmount() {
        return this.targetAmount;
    }

    @Override
    public double getCurrentAmount() {
        return this.savedAmount;
    }

    /**
     * Remaining savings required to complete the goal.
     */
    public double getRemainingAmount() {
        return Math.max(0, this.targetAmount - this.savedAmount);
    }

    /**
     * Checks if the deadline has passed.
     */
    public boolean isOverdue() {
        return deadline != null && LocalDate.now().isAfter(deadline) && !"COMPLETED".equalsIgnoreCase(status);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
