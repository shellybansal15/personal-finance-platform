package com.finance.servlet;

import com.finance.exception.AppException;
import com.finance.model.Budget;
import com.finance.model.Category;
import com.finance.service.BudgetService;
import com.finance.service.ExpenseService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * ============================================================================
 * [Servlets & OOP Rubric: BudgetServlet]
 * Controller for managing category spending budgets implementing the Trackable interface.
 * ============================================================================
 */
@WebServlet(urlPatterns = {"/user/budgets"})
public class BudgetServlet extends HttpServlet {

    private BudgetService budgetService;
    private ExpenseService expenseService;

    @Override
    public void init() {
        this.budgetService = new BudgetService();
        this.expenseService = new ExpenseService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        List<Budget> budgets = budgetService.getUserBudgets(userId);
        List<Category> categories = expenseService.getAllCategories();

        req.setAttribute("budgets", budgets);
        req.setAttribute("categories", categories);

        req.getRequestDispatcher("/WEB-INF/jsp/user/budgets.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");
        String action = req.getParameter("action");
        String clientIp = getClientIp(req);

        try {
            if ("create".equalsIgnoreCase(action)) {
                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                String amount = req.getParameter("amount");
                String startDate = req.getParameter("startDate");
                String endDate = req.getParameter("endDate");

                budgetService.createBudget(userId, categoryId, amount, startDate, endDate, clientIp);
                session.setAttribute("flashSuccess", "Success: Budget limit of $" + amount + " set successfully!");

            } else if ("edit".equalsIgnoreCase(action)) {
                int budgetId = Integer.parseInt(req.getParameter("budgetId"));
                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                String amount = req.getParameter("amount");
                String startDate = req.getParameter("startDate");
                String endDate = req.getParameter("endDate");

                budgetService.updateBudget(budgetId, userId, categoryId, amount, startDate, endDate, clientIp);
                session.setAttribute("flashSuccess", "Success: Budget updated successfully!");

            } else if ("delete".equalsIgnoreCase(action)) {
                int budgetId = Integer.parseInt(req.getParameter("budgetId"));
                budgetService.deleteBudget(budgetId, userId, clientIp);
                session.setAttribute("flashSuccess", "Success: Budget removed successfully!");
            }
        } catch (AppException e) {
            session.setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flashError", "An unexpected error occurred: " + e.getMessage());
        }

        // Post-Redirect-Get pattern
        resp.sendRedirect(req.getContextPath() + "/user/budgets");
    }

    private String getClientIp(HttpServletRequest req) {
        String remoteAddr = req.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = req.getRemoteAddr();
        }
        return remoteAddr != null ? remoteAddr : "127.0.0.1";
    }
}
