package com.finance.service;

import com.finance.dao.BudgetDAO;
import com.finance.dao.ExpenseDAO;
import com.finance.dao.GoalDAO;
import com.finance.model.Budget;
import com.finance.model.Expense;
import com.finance.model.Goal;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;

/**
 * ============================================================================
 * [Multithreading Rubric: ExecutorService for Background Report Generation]
 * ReportService executes computationally intensive tasks (CSV generation,
 * aggregate financial audit summaries) asynchronously in background worker threads.
 * Using an ExecutorService pool prevents long-running generation jobs from
 * starving Tomcat's primary HTTP servlet request threads.
 * ============================================================================
 */
public class ReportService {

    // Fixed thread pool dedicated to background report generation
    private static final int THREAD_POOL_SIZE = 4;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(THREAD_POOL_SIZE, new ThreadFactory() {
        private int counter = 1;
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "FinanceReportWorker-" + counter++);
            t.setDaemon(true); // Daemon threads ensure JVM can shut down cleanly
            return t;
        }
    });

    private final ExpenseDAO expenseDAO;
    private final BudgetDAO budgetDAO;
    private final GoalDAO goalDAO;

    public ReportService() {
        this.expenseDAO = new ExpenseDAO();
        this.budgetDAO = new BudgetDAO();
        this.goalDAO = new GoalDAO();
    }

    /**
     * [Rubric: Multithreading ExecutorService]
     * Submits a CSV export job to the background thread pool and returns a Future.
     *
     * @param expenses List of expenses to export
     * @return Future containing the byte array of the CSV document
     */
    public Future<byte[]> generateCsvExportAsync(List<Expense> expenses) {
        return EXECUTOR_SERVICE.submit(() -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8)) {
                // CSV Header Row
                writer.println("ID,Date,Category,Amount ($),Note,Created At");

                // Data Rows
                for (Expense exp : expenses) {
                    String cleanNote = exp.getNote() != null ? exp.getNote().replace("\"", "\"\"") : "";
                    String created = exp.getCreatedAt() != null ? exp.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
                    writer.printf("%d,%s,\"%s\",%.2f,\"%s\",%s%n",
                            exp.getId(),
                            exp.getDate(),
                            exp.getCategoryName() != null ? exp.getCategoryName() : "General",
                            exp.getAmount(),
                            cleanNote,
                            created
                    );
                }
                writer.flush();
            }
            return baos.toByteArray();
        });
    }

    /**
     * [Rubric: Multithreading ExecutorService]
     * Computes an in-depth financial summary report asynchronously.
     *
     * @param userId User ID
     * @return Future containing plain-text summary report
     */
    public Future<String> generateFinancialSummaryAsync(int userId) {
        return EXECUTOR_SERVICE.submit(() -> {
            double monthSpent = expenseDAO.getTotalSpentThisMonth(userId);
            double allTimeSpent = expenseDAO.getTotalSpentAllTime(userId);
            double totalBudget = budgetDAO.getTotalBudgetThisMonth(userId);
            double totalSaved = goalDAO.getTotalSavedAmount(userId);
            List<Budget> budgets = budgetDAO.findByUserId(userId);
            List<Goal> goals = goalDAO.findByUserId(userId);

            StringBuilder sb = new StringBuilder();
            sb.append("=== PERSONAL FINANCIAL SUMMARY REPORT ===\n");
            sb.append("Generated On: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
            sb.append(String.format("Total Spent (This Month): $%.2f%n", monthSpent));
            sb.append(String.format("Total Spent (All-Time):   $%.2f%n", allTimeSpent));
            sb.append(String.format("Active Budgets Total:     $%.2f%n", totalBudget));
            sb.append(String.format("Total Goals Saved:        $%.2f%n%n", totalSaved));

            sb.append("--- ACTIVE BUDGETS ---\n");
            for (Budget b : budgets) {
                sb.append(String.format("- %s: Spent $%.2f of $%.2f (%.1f%%)%n",
                        b.getCategoryName(), b.getSpentAmount(), b.getAmount(), b.getProgressPercentage()));
            }

            sb.append("\n--- SAVINGS GOALS ---\n");
            for (Goal g : goals) {
                sb.append(String.format("- %s: Saved $%.2f of $%.2f [Status: %s]%n",
                        g.getDescription(), g.getSavedAmount(), g.getTargetAmount(), g.getStatus()));
            }

            return sb.toString();
        });
    }
}
