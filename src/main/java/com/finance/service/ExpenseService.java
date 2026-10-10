package com.finance.service;

import com.finance.dao.CategoryDAO;
import com.finance.dao.ExpenseDAO;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.exception.ValidationException;
import com.finance.model.Category;
import com.finance.model.Expense;
import com.finance.util.AuditLogger;
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

    public Expense addExpense(int userId, int categoryId, String amountStr, String dateStr, String note, String ipAddress) {
        double amount = ValidationUtil.validatePositiveAmount(amountStr, "Expense amount");
        LocalDate date = ValidationUtil.validateDate(dateStr, "Expense date");

        if (categoryDAO.findById(categoryId).isEmpty()) {
            throw new ValidationException("Selected category does not exist.");
        }

        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setCategoryId(categoryId);
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setNote(note != null ? note.trim() : "");

        expenseDAO.save(expense);
        auditLogger.log(userId, String.format("Added expense of $%.2f (ID #%d)", amount, expense.getId()), ipAddress);

        return expense;
    }

    public void updateExpense(int expenseId, int userId, int categoryId, String amountStr, String dateStr, String note, String ipAddress) {
        double amount = ValidationUtil.validatePositiveAmount(amountStr, "Expense amount");
        LocalDate date = ValidationUtil.validateDate(dateStr, "Expense date");

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

        expenseDAO.update(exp);
        auditLogger.log(userId, String.format("Updated expense ID #%d to $%.2f", expenseId, amount), ipAddress);
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
        int offset = Math.max(0, (page - 1) * pageSize);
        return expenseDAO.findByUserId(userId, categoryId, startDate, endDate, offset, pageSize);
    }

    public int countFilteredExpenses(int userId, Integer categoryId, LocalDate startDate, LocalDate endDate) {
        return expenseDAO.countByUserId(userId, categoryId, startDate, endDate);
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

    public Map<String, Double> getMonthlyTrend(int userId, int months) {
        return expenseDAO.getMonthlySpendingTrend(userId, months);
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }
}
