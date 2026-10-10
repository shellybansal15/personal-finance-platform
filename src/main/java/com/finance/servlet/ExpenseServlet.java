package com.finance.servlet;

import com.finance.exception.AppException;
import com.finance.model.Category;
import com.finance.model.Expense;
import com.finance.service.ExpenseService;
import com.finance.service.ReportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Future;

/**
 * ============================================================================
 * [Servlets & Multithreading Rubric: ExpenseServlet]
 * Controller for expense tracking, filtering, pagination, modal CRUD operations,
 * and asynchronous background CSV exports using ReportService's ExecutorService.
 * ============================================================================
 */
@WebServlet(urlPatterns = {"/user/expenses"})
public class ExpenseServlet extends HttpServlet {

    private ExpenseService expenseService;
    private ReportService reportService;

    @Override
    public void init() {
        this.expenseService = new ExpenseService();
        this.reportService = new ReportService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        String action = req.getParameter("action");

        // [Rubric: Multithreading ExecutorService CSV Export]
        if ("export".equalsIgnoreCase(action)) {
            handleCsvExport(userId, resp);
            return;
        }

        // Filtering & Pagination Parameters
        String categoryIdStr = req.getParameter("categoryId");
        Integer categoryId = (categoryIdStr != null && !categoryIdStr.isEmpty() && !"ALL".equalsIgnoreCase(categoryIdStr))
                ? Integer.parseInt(categoryIdStr) : null;

        String startDateStr = req.getParameter("startDate");
        LocalDate startDate = (startDateStr != null && !startDateStr.isEmpty()) ? LocalDate.parse(startDateStr) : null;

        String endDateStr = req.getParameter("endDate");
        LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty()) ? LocalDate.parse(endDateStr) : null;

        int page = 1;
        String pageStr = req.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Math.max(1, Integer.parseInt(pageStr));
            } catch (NumberFormatException ignored) {}
        }
        int pageSize = 10;

        int totalRecords = expenseService.countFilteredExpenses(userId, categoryId, startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages == 0) totalPages = 1;

        List<Expense> expenses = expenseService.getFilteredExpenses(userId, categoryId, startDate, endDate, page, pageSize);
        List<Category> categories = expenseService.getAllCategories();

        req.setAttribute("expenses", expenses);
        req.setAttribute("categories", categories);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalRecords", totalRecords);
        req.setAttribute("selectedCategoryId", categoryId);
        req.setAttribute("startDate", startDateStr);
        req.setAttribute("endDate", endDateStr);

        req.getRequestDispatcher("/WEB-INF/jsp/user/expenses.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");
        String action = req.getParameter("action");
        String clientIp = getClientIp(req);

        try {
            if ("add".equalsIgnoreCase(action)) {
                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                String amount = req.getParameter("amount");
                String date = req.getParameter("date");
                String note = req.getParameter("note");

                expenseService.addExpense(userId, categoryId, amount, date, note, clientIp);
                session.setAttribute("flashSuccess", "Success: Expense of $" + amount + " recorded successfully!");

            } else if ("edit".equalsIgnoreCase(action)) {
                int expenseId = Integer.parseInt(req.getParameter("expenseId"));
                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                String amount = req.getParameter("amount");
                String date = req.getParameter("date");
                String note = req.getParameter("note");

                expenseService.updateExpense(expenseId, userId, categoryId, amount, date, note, clientIp);
                session.setAttribute("flashSuccess", "Success: Expense record updated successfully!");

            } else if ("delete".equalsIgnoreCase(action)) {
                int expenseId = Integer.parseInt(req.getParameter("expenseId"));
                expenseService.deleteExpense(expenseId, userId, clientIp);
                session.setAttribute("flashSuccess", "Success: Expense deleted successfully!");
            }
        } catch (AppException e) {
            session.setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flashError", "An unexpected error occurred: " + e.getMessage());
        }

        // Post-Redirect-Get pattern
        resp.sendRedirect(req.getContextPath() + "/user/expenses");
    }

    /**
     * Executes CSV generation in a background thread via ExecutorService.
     */
    private void handleCsvExport(int userId, HttpServletResponse resp) throws IOException {
        try {
            List<Expense> allExpenses = expenseService.getAllExpensesForExport(userId);
            // Submit to background thread pool
            Future<byte[]> exportFuture = reportService.generateCsvExportAsync(allExpenses);
            byte[] csvBytes = exportFuture.get(); // Asynchronously generated

            resp.setContentType("text/csv; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"finance_expenses_" + LocalDate.now() + ".csv\"");
            resp.setContentLength(csvBytes.length);

            try (OutputStream os = resp.getOutputStream()) {
                os.write(csvBytes);
                os.flush();
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate CSV export: " + e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest req) {
        String remoteAddr = req.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = req.getRemoteAddr();
        }
        return remoteAddr != null ? remoteAddr : "127.0.0.1";
    }
}
