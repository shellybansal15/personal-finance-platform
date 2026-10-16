package com.finance.service;

import com.finance.dao.CategoryDAO;
import com.finance.dao.ExpenseDAO;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.exception.ValidationException;
import com.finance.model.Category;
import com.finance.model.Expense;
import com.finance.model.PaymentMode;
import com.finance.util.AuditLogger;
import com.finance.util.CurrencyUtil;
import com.finance.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================================
 * [Service Rubric: ExpenseService]
 * Business logic layer for expenses: validation, ownership verification,
 * filtering, aggregations, and security audit tracking.
 * Updated: Handles paymentMode field, paymentMode filter, and spending-by-mode aggregation.
 * ============================================================================
 */
public class ExpenseService {

    private final ExpenseDAO expenseDAO;
    private final CategoryDAO categoryDAO;
    private final AuditLogger auditLogger;

    public ExpenseService() {
        this.expenseDAO = new ExpenseDAO();
        this.categoryDAO = new CategoryDAO();
        this.auditLogger = AuditLogger.getInstance();
    }

    public Expense addExpense(int userId, int categoryId, String amountStr, String dateStr, String note, String paymentModeStr, String ipAddress) {
        double amount = ValidationUtil.validatePositiveAmount(amountStr, "Expense amount");
        LocalDate date = ValidationUtil.validateDate(dateStr, "Expense date");
        PaymentMode paymentMode = PaymentMode.fromString(paymentModeStr);

        if (categoryDAO.findById(categoryId).isEmpty()) {
            throw new ValidationException("Selected category does not exist.");
        }

        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setCategoryId(categoryId);
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setNote(note != null ? note.trim() : "");
        expense.setPaymentMode(paymentMode);

        expenseDAO.save(expense);
        auditLogger.log(userId, String.format("Added expense of %s via %s (ID #%d)", CurrencyUtil.format(amount), paymentMode.getDisplayName(), expense.getId()), ipAddress);

        return expense;
    }

    // Overload for backward compatibility (defaults to UPI)
    public Expense addExpense(int userId, int categoryId, String amountStr, String dateStr, String note, String ipAddress) {
        return addExpense(userId, categoryId, amountStr, dateStr, note, "UPI", ipAddress);
    }

    public void updateExpense(int expenseId, int userId, int categoryId, String amountStr, String dateStr, String note, String paymentModeStr, String ipAddress) {
        double amount = ValidationUtil.validatePositiveAmount(amountStr, "Expense amount");
        LocalDate date = ValidationUtil.validateDate(dateStr, "Expense date");
        PaymentMode paymentMode = PaymentMode.fromString(paymentModeStr);

        Optional<Expense> existing = expenseDAO.findById(expenseId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Expense record not found.");
        }
        if (existing.get().getUserId() != userId) {
            throw new UnauthorizedException("You are not authorized to edit this expense.");
        }
        if (categoryDAO.findById(categoryId).isEmpty()) {
            throw new ValidationException("Selected category does not exist.");
        }

        Expense exp = existing.get();
        exp.setCategoryId(categoryId);
        exp.setAmount(amount);
        exp.setDate(date);
        exp.setNote(note != null ? note.trim() : "");
        exp.setPaymentMode(paymentMode);

        expenseDAO.update(exp);
        auditLogger.log(userId, String.format("Updated expense ID #%d to %s via %s", expenseId, CurrencyUtil.format(amount), paymentMode.getDisplayName()), ipAddress);
    }

    // Overload for backward compatibility
    public void updateExpense(int expenseId, int userId, int categoryId, String amountStr, String dateStr, String note, String ipAddress) {
        updateExpense(expenseId, userId, categoryId, amountStr, dateStr, note, "UPI", ipAddress);
    }

    public void deleteExpense(int expenseId, int userId, String ipAddress) {
        Optional<Expense> existing = expenseDAO.findById(expenseId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Expense record not found.");
        }
        if (existing.get().getUserId() != userId) {
            throw new UnauthorizedException("You are not authorized to delete this expense.");
        }

        expenseDAO.deleteUserExpense(expenseId, userId);
        auditLogger.log(userId, "Deleted expense ID #" + expenseId, ipAddress);
    }

    public List<Expense> getFilteredExpenses(int userId, Integer categoryId, LocalDate startDate, LocalDate endDate, int page, int pageSize) {
        return getFilteredExpenses(userId, categoryId, startDate, endDate, null, page, pageSize);
    }

    public List<Expense> getFilteredExpenses(int userId, Integer categoryId, LocalDate startDate, LocalDate endDate, String paymentMode, int page, int pageSize) {
        int offset = Math.max(0, (page - 1) * pageSize);
        return expenseDAO.findByUserId(userId, categoryId, startDate, endDate, paymentMode, offset, pageSize);
    }

    public int countFilteredExpenses(int userId, Integer categoryId, LocalDate startDate, LocalDate endDate) {
        return expenseDAO.countByUserId(userId, categoryId, startDate, endDate);
    }

    public int countFilteredExpenses(int userId, Integer categoryId, LocalDate startDate, LocalDate endDate, String paymentMode) {
        return expenseDAO.countByUserId(userId, categoryId, startDate, endDate, paymentMode);
    }

    public List<Expense> getAllExpensesForExport(int userId) {
        return expenseDAO.findByUserId(userId, null, null, null, 0, 10000);
    }

    public double getMonthSpent(int userId) {
        return expenseDAO.getTotalSpentThisMonth(userId);
    }

    public double getAllTimeSpent(int userId) {
        return expenseDAO.getTotalSpentAllTime(userId);
    }

    public Map<String, Double> getSpendingByCategory(int userId) {
        return expenseDAO.getSpendingByCategory(userId);
    }

    public Map<String, Double> getSpendingByPaymentMode(int userId) {
        return expenseDAO.getSpendingByPaymentMode(userId);
    }

    public Map<String, Double> getMonthlyTrend(int userId, int months) {
        return expenseDAO.getMonthlySpendingTrend(userId, months);
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public PaymentMode[] getAllPaymentModes() {
        return PaymentMode.values();
    }
}
