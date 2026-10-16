package com.finance.model;

import com.finance.util.CurrencyUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * [OOP Rubric: Abstract Class & Inheritance]
 * Abstract Transaction serves as the base class for all monetary movements.
 * Demonstrates inheritance and polymorphism in financial records.
 * Child classes (Expense, Income) must provide concrete implementations of
 * abstract methods such as getType().
 * ============================================================================
 */
public abstract class Transaction {

    private int id;
    private int userId;
    private double amount;
    private LocalDate date;
    private String note;
    private LocalDateTime createdAt;

    public Transaction() {
    }

    public Transaction(int id, int userId, double amount, LocalDate date, String note, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.createdAt = createdAt;
    }

    // [Polymorphism] Abstract method implemented differently by Expense and Income
    public abstract String getType();

    // Common descriptive method demonstrating code reuse in inheritance
    public String describe() {
        return String.format("[%s] Amount: %s on %s", getType(), CurrencyUtil.format(amount), date);
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
