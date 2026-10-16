package com.finance.servlet;

import com.finance.model.Budget;
import com.finance.model.Category;
import com.finance.model.Expense;
import com.finance.model.Goal;
import com.finance.model.PaymentMode;
import com.finance.service.BudgetService;
import com.finance.service.ExpenseService;
import com.finance.service.GoalService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * [Servlets Rubric: UserDashboardServlet]
 * Gathers aggregate financial metrics, recent transactions, active budgets,
 * goals, and data maps for Chart.js rendering on the user overview dashboard.
 * Updated: Adds payment mode spending map and paymentModes enum for modal dropdowns.
 * ============================================================================
 */
@WebServlet(urlPatterns = {"/user/dashboard"})
public class UserDashboardServlet extends HttpServlet {

    private ExpenseService expenseService;
    private BudgetService budgetService;
    private GoalService goalService;

    @Override
    public void init() {
        this.expenseService = new ExpenseService();
        this.budgetService = new BudgetService();
        this.goalService = new GoalService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        // 1. Four Stat Card Metrics
        double monthSpent = expenseService.getMonthSpent(userId);
        double activeBudgetTotal = budgetService.getTotalActiveBudget(userId);
        double totalSaved = goalService.getTotalSaved(userId);
        double allTimeSpent = expenseService.getAllTimeSpent(userId);

        req.setAttribute("monthSpent", monthSpent);
        req.setAttribute("activeBudgetTotal", activeBudgetTotal);
        req.setAttribute("totalSaved", totalSaved);
        req.setAttribute("allTimeSpent", allTimeSpent);

        // 2. Chart.js Data Maps [Rubric: Collections Map<K,V>]
        Map<String, Double> categorySpending = expenseService.getSpendingByCategory(userId);
        Map<String, Double> monthlyTrend = expenseService.getMonthlyTrend(userId, 6);
        // New: Payment Mode spending map for doughnut chart
        Map<String, Double> paymentModeSpending = expenseService.getSpendingByPaymentMode(userId);

        req.setAttribute("categorySpending", categorySpending);
        req.setAttribute("monthlyTrend", monthlyTrend);
        req.setAttribute("paymentModeSpending", paymentModeSpending);

        // 3. Recent 5 Transactions
        List<Expense> recentExpenses = expenseService.getFilteredExpenses(userId, null, null, null, 1, 5);
        req.setAttribute("recentExpenses", recentExpenses);

        // 4. Budgets and Goals (Trackables)
        List<Budget> budgets = budgetService.getUserBudgets(userId);
        List<Goal> goals = goalService.getUserGoals(userId);

        req.setAttribute("budgets", budgets);
        req.setAttribute("goals", goals);

        // 5. Available Categories & Payment Modes for Add Expense Modal
        List<Category> categories = expenseService.getAllCategories();
        req.setAttribute("categories", categories);
        req.setAttribute("paymentModes", PaymentMode.values());

        req.getRequestDispatcher("/WEB-INF/jsp/user/dashboard.jsp").forward(req, resp);
    }
}
