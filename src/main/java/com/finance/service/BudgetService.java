package com.finance.service;

import com.finance.dao.BudgetDAO;
import com.finance.dao.CategoryDAO;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.exception.ValidationException;
import com.finance.model.Budget;
import com.finance.util.AuditLogger;
import com.finance.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * [Service & OOP Rubric: BudgetService]
 * Business logic for budgeting: creates, updates, and checks limits for Trackable budgets.
 * ============================================================================
 */
public class BudgetService {

    private final BudgetDAO budgetDAO;
    private final CategoryDAO categoryDAO;
    private final AuditLogger auditLogger;

    public BudgetService() {
        this.budgetDAO = new BudgetDAO();
        this.categoryDAO = new CategoryDAO();
        this.auditLogger = AuditLogger.getInstance();
    }

    public Budget createBudget(int userId, int categoryId, String amountStr, String startDateStr, String endDateStr, String ipAddress) {
        double amount = ValidationUtil.validatePositiveAmount(amountStr, "Budget amount");
        LocalDate startDate = ValidationUtil.validateDate(startDateStr, "Start date");
        LocalDate endDate = ValidationUtil.validateDate(endDateStr, "End date");

        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date cannot be prior to start date.");
        }
        if (categoryDAO.findById(categoryId).isEmpty()) {
            throw new ValidationException("Selected category does not exist.");
        }

        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setCategoryId(categoryId);
        budget.setAmount(amount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);

        budgetDAO.save(budget);
        auditLogger.log(userId, String.format("Created budget of $%.2f for Category ID #%d", amount, categoryId), ipAddress);

        return budget;
    }

    public void updateBudget(int budgetId, int userId, int categoryId, String amountStr, String startDateStr, String endDateStr, String ipAddress) {
        double amount = ValidationUtil.validatePositiveAmount(amountStr, "Budget amount");
        LocalDate startDate = ValidationUtil.validateDate(startDateStr, "Start date");
        LocalDate endDate = ValidationUtil.validateDate(endDateStr, "End date");

        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date cannot be prior to start date.");
        }

        Optional<Budget> existing = budgetDAO.findById(budgetId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Budget record not found.");
        }
        if (existing.get().getUserId() != userId) {
            throw new UnauthorizedException("You are not authorized to update this budget.");
        }
        if (categoryDAO.findById(categoryId).isEmpty()) {
            throw new ValidationException("Selected category does not exist.");
        }

        Budget b = existing.get();
        b.setCategoryId(categoryId);
        b.setAmount(amount);
        b.setStartDate(startDate);
        b.setEndDate(endDate);

        budgetDAO.update(b);
        auditLogger.log(userId, String.format("Updated budget ID #%d to $%.2f", budgetId, amount), ipAddress);
    }

    public void deleteBudget(int budgetId, int userId, String ipAddress) {
        Optional<Budget> existing = budgetDAO.findById(budgetId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Budget record not found.");
        }
        if (existing.get().getUserId() != userId) {
            throw new UnauthorizedException("You are not authorized to delete this budget.");
        }

        budgetDAO.deleteUserBudget(budgetId, userId);
        auditLogger.log(userId, "Deleted budget ID #" + budgetId, ipAddress);
    }

    public List<Budget> getUserBudgets(int userId) {
        return budgetDAO.findByUserId(userId);
    }

    public double getTotalActiveBudget(int userId) {
        return budgetDAO.getTotalBudgetThisMonth(userId);
    }
}
