package com.finance.service;

import com.finance.dao.GoalDAO;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.exception.ValidationException;
import com.finance.model.Goal;
import com.finance.util.AuditLogger;
import com.finance.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * [Service & OOP Rubric: GoalService]
 * Business logic for financial goals: creates, funds, and updates Trackable goals.
 * ============================================================================
 */
public class GoalService {

    private final GoalDAO goalDAO;
    private final AuditLogger auditLogger;

    public GoalService() {
        this.goalDAO = new GoalDAO();
        this.auditLogger = AuditLogger.getInstance();
    }

    public Goal createGoal(int userId, String description, String targetAmountStr, String initialSavedStr, String deadlineStr, String ipAddress) {
        String cleanDesc = ValidationUtil.requireNonEmpty(description, "Goal description");
        double target = ValidationUtil.validatePositiveAmount(targetAmountStr, "Target amount");
        double saved = ValidationUtil.validateNonNegativeAmount(initialSavedStr, "Initial saved amount");
        LocalDate deadline = ValidationUtil.validateDate(deadlineStr, "Goal deadline");

        Goal goal = new Goal();
        goal.setUserId(userId);
        goal.setDescription(cleanDesc);
        goal.setTargetAmount(target);
        goal.setSavedAmount(saved);
        goal.setDeadline(deadline);
        goal.setStatus(saved >= target ? "COMPLETED" : "IN_PROGRESS");

        goalDAO.save(goal);
        auditLogger.log(userId, String.format("Created Goal: %s (Target: $%.2f)", cleanDesc, target), ipAddress);

        return goal;
    }

    public void updateGoal(int goalId, int userId, String description, String targetAmountStr, String deadlineStr, String status, String ipAddress) {
        String cleanDesc = ValidationUtil.requireNonEmpty(description, "Goal description");
        double target = ValidationUtil.validatePositiveAmount(targetAmountStr, "Target amount");
        LocalDate deadline = ValidationUtil.validateDate(deadlineStr, "Goal deadline");

        Optional<Goal> existing = goalDAO.findById(goalId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Goal record not found.");
        }
        if (existing.get().getUserId() != userId) {
            throw new UnauthorizedException("You are not authorized to update this goal.");
        }

        Goal g = existing.get();
        g.setDescription(cleanDesc);
        g.setTargetAmount(target);
        g.setDeadline(deadline);
        if (status != null && !status.trim().isEmpty()) {
            g.setStatus(status.trim().toUpperCase());
        }

        goalDAO.update(g);
        auditLogger.log(userId, String.format("Updated Goal ID #%d: %s", goalId, cleanDesc), ipAddress);
    }

    public void addFunds(int goalId, int userId, String depositAmountStr, String ipAddress) {
        double deposit = ValidationUtil.validatePositiveAmount(depositAmountStr, "Deposit amount");

        Optional<Goal> existing = goalDAO.findById(goalId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Goal record not found.");
        }
        if (existing.get().getUserId() != userId) {
            throw new UnauthorizedException("You are not authorized to deposit to this goal.");
        }

        goalDAO.addFunds(goalId, deposit, userId);
        auditLogger.log(userId, String.format("Deposited $%.2f into Goal ID #%d", deposit, goalId), ipAddress);
    }

    public void deleteGoal(int goalId, int userId, String ipAddress) {
        Optional<Goal> existing = goalDAO.findById(goalId);
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("Goal record not found.");
        }
        if (existing.get().getUserId() != userId) {
            throw new UnauthorizedException("You are not authorized to delete this goal.");
        }

        goalDAO.deleteUserGoal(goalId, userId);
        auditLogger.log(userId, "Deleted Goal ID #" + goalId, ipAddress);
    }

    public List<Goal> getUserGoals(int userId) {
        return goalDAO.findByUserId(userId);
    }

    public double getTotalSaved(int userId) {
        return goalDAO.getTotalSavedAmount(userId);
    }
}
