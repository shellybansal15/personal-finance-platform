package com.finance.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * [OOP Rubric: Inheritance & Polymorphism]
 * Expense extends Transaction, adding category metadata and overriding getType().
 * Represents money spent by a user.
 * Updated: Added paymentMode field for payment mode tracking.
 * ============================================================================
 */
public class Expense extends Transaction {

    private int categoryId;
    private String categoryName;
    private PaymentMode paymentMode; // New: Payment mode for this expense

    public Expense() {
        super();
        this.paymentMode = PaymentMode.UPI; // Default
    }

    public Expense(int id, int userId, int categoryId, double amount, LocalDate date, String note, LocalDateTime createdAt) {
        this(id, userId, categoryId, amount, date, note, PaymentMode.UPI, createdAt);
    }

    public Expense(int id, int userId, int categoryId, double amount, LocalDate date, String note, PaymentMode paymentMode, LocalDateTime createdAt) {
        super(id, userId, amount, date, note, createdAt);
        this.categoryId = categoryId;
        this.paymentMode = paymentMode != null ? paymentMode : PaymentMode.UPI;
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

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode != null ? paymentMode : PaymentMode.UPI;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + getId() +
                ", amount=" + getAmount() +
                ", category='" + categoryName + '\'' +
                ", paymentMode=" + paymentMode +
                ", date=" + getDate() +
                '}';
    }
}
