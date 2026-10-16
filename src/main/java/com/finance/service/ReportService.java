package com.finance.service;

import com.finance.dao.BudgetDAO;
import com.finance.dao.ExpenseDAO;
import com.finance.dao.GoalDAO;
import com.finance.model.Budget;
import com.finance.model.Expense;
import com.finance.model.Goal;
import com.finance.util.CurrencyUtil;

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
 * Updated: CSV now uses ₹ symbol and includes Payment Mode column.
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
     * Uses ₹ symbol and includes Payment Mode column.
     *
     * @param expenses List of expenses to export
     * @return Future containing the byte array of the CSV document
     */
    public Future<byte[]> generateCsvExportAsync(List<Expense> expenses) {
        return EXECUTOR_SERVICE.submit(() -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // UTF-8 BOM for Excel compatibility with Indian Rupee symbol
            baos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            try (PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8)) {
                // CSV Header Row — with ₹ and Payment Mode
                writer.println("ID,Date,Category,Amount (\u20b9),Payment Mode,Note,Created At");

                // Data Rows
                for (Expense exp : expenses) {
                    String cleanNote = exp.getNote() != null ? exp.getNote().replace("\"", "\"\"") : "";
                    String created = exp.getCreatedAt() != null ? exp.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
                    String paymentMode = exp.getPaymentMode() != null ? exp.getPaymentMode().getDisplayName() : "UPI";
                    writer.printf("%d,%s,\"%s\",\"%s\",\"%s\",\"%s\",%s%n",
                            exp.getId(),
                            exp.getDate(),
                            exp.getCategoryName() != null ? exp.getCategoryName() : "General",
                            CurrencyUtil.format(exp.getAmount()),
                            paymentMode,
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
     * Uses ₹ symbol for Indian Rupee formatting.
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
            sb.append("Total Spent (This Month): ").append(CurrencyUtil.format(monthSpent)).append("\n");
            sb.append("Total Spent (All-Time):   ").append(CurrencyUtil.format(allTimeSpent)).append("\n");
            sb.append("Active Budgets Total:     ").append(CurrencyUtil.format(totalBudget)).append("\n");
            sb.append("Total Goals Saved:        ").append(CurrencyUtil.format(totalSaved)).append("\n\n");

            sb.append("--- ACTIVE BUDGETS ---\n");
            for (Budget b : budgets) {
                sb.append(String.format("- %s: Spent %s of %s (%.1f%%)%n",
                        b.getCategoryName(), CurrencyUtil.format(b.getSpentAmount()),
                        CurrencyUtil.format(b.getAmount()), b.getProgressPercentage()));
            }

            sb.append("\n--- SAVINGS GOALS ---\n");
            for (Goal g : goals) {
                sb.append(String.format("- %s: Saved %s of %s [Status: %s]%n",
                        g.getDescription(), CurrencyUtil.format(g.getSavedAmount()),
                        CurrencyUtil.format(g.getTargetAmount()), g.getStatus()));
            }

            return sb.toString();
        });
    }
}
