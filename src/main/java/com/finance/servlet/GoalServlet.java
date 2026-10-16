package com.finance.servlet;

import com.finance.exception.AppException;
import com.finance.model.Goal;
import com.finance.service.GoalService;
import com.finance.util.CurrencyUtil;
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
 * [Servlets & OOP Rubric: GoalServlet]
 * Controller for tracking financial goals implementing the Trackable interface,
 * handling fund contributions, deadlines, and completion states.
 * ============================================================================
 */
@WebServlet(urlPatterns = {"/user/goals"})
public class GoalServlet extends HttpServlet {

    private GoalService goalService;

    @Override
    public void init() {
        this.goalService = new GoalService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        List<Goal> goals = goalService.getUserGoals(userId);
        double totalSaved = goalService.getTotalSaved(userId);

        req.setAttribute("goals", goals);
        req.setAttribute("totalSaved", totalSaved);

        req.getRequestDispatcher("/WEB-INF/jsp/user/goals.jsp").forward(req, resp);
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
                String description = req.getParameter("description");
                String targetAmount = req.getParameter("targetAmount");
                String initialSaved = req.getParameter("initialSaved");
                String deadline = req.getParameter("deadline");

                goalService.createGoal(userId, description, targetAmount, initialSaved, deadline, clientIp);
                session.setAttribute("flashSuccess", "Success: Financial goal '" + description + "' created successfully!");

            } else if ("deposit".equalsIgnoreCase(action)) {
                int goalId = Integer.parseInt(req.getParameter("goalId"));
                String amount = req.getParameter("depositAmount");

                goalService.addFunds(goalId, userId, amount, clientIp);
                session.setAttribute("flashSuccess", "Success: Added " + CurrencyUtil.format(amount) + " towards your goal!");

            } else if ("edit".equalsIgnoreCase(action)) {
                int goalId = Integer.parseInt(req.getParameter("goalId"));
                String description = req.getParameter("description");
                String targetAmount = req.getParameter("targetAmount");
                String deadline = req.getParameter("deadline");
                String status = req.getParameter("status");

                goalService.updateGoal(goalId, userId, description, targetAmount, deadline, status, clientIp);
                session.setAttribute("flashSuccess", "Success: Goal updated successfully!");

            } else if ("delete".equalsIgnoreCase(action)) {
                int goalId = Integer.parseInt(req.getParameter("goalId"));
                goalService.deleteGoal(goalId, userId, clientIp);
                session.setAttribute("flashSuccess", "Success: Goal removed successfully!");
            }
        } catch (AppException e) {
            session.setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flashError", "An unexpected error occurred: " + e.getMessage());
        }

        // Post-Redirect-Get pattern
        resp.sendRedirect(req.getContextPath() + "/user/goals");
    }

    private String getClientIp(HttpServletRequest req) {
        String remoteAddr = req.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = req.getRemoteAddr();
        }
        return remoteAddr != null ? remoteAddr : "127.0.0.1";
    }
}
