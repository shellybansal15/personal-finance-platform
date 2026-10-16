package com.finance.util;

import com.finance.dao.BudgetDAO;
import com.finance.dao.CategoryDAO;
import com.finance.dao.ExpenseDAO;
import com.finance.dao.GoalDAO;
import com.finance.dao.SecurityLogDAO;
import com.finance.dao.SystemSettingDAO;
import com.finance.dao.UserDAO;
import com.finance.exception.DatabaseException;
import com.finance.model.Budget;
import com.finance.model.Category;
import com.finance.model.Expense;
import com.finance.model.Goal;
import com.finance.model.PaymentMode;
import com.finance.model.User;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Ensures demo login accounts and rich MVP mock data exist whenever the
 * application starts (and again as a safety net before demo logins).
 * Idempotent: skips rows that are already present so restarts never duplicate data.
 * Helps cloud hosts (e.g. Render) where the DB may come up empty after a redeploy.
 */
@WebListener
public class DatabaseSeeder implements ServletContextListener {

    public static final String DEMO_ADMIN_EMAIL = "admin@finance.com";
    public static final String DEMO_USER_EMAIL = "user@finance.com";
    /** Plain-text passwords matching login.jsp Fill buttons / README / seed.sql. */
    public static final String DEMO_ADMIN_PASSWORD = "Admin@123";
    public static final String DEMO_USER_PASSWORD = "User@123";

    private static final int MAX_ATTEMPTS = 5;
    private static final long RETRY_DELAY_MS = 2000L;

    /** Pre-computed BCrypt hashes matching README / seed.sql demo passwords. */
    private static final String ADMIN_HASH =
            "$2a$10$TeuGs1SqL6BgDJ3tVKUXpuqsNcNZGKSLS6OUTqBTH9JkNnEPYonci"; // Admin@123
    private static final String USER_HASH =
            "$2a$10$.Eq/WSEzxYEeLi2aGtIH6eYWyxhMD3QHQme1U.m4gGLzkXypBYwBy"; // User@123

    private static final Object SEED_LOCK = new Object();
    private static volatile boolean seedCompleted;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[DatabaseSeeder] Ensuring demo accounts and MVP data exist…");
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                ensureDemoWorkspace();
                System.out.println("[DatabaseSeeder] Demo workspace check complete.");
                return;
            } catch (Exception e) {
                System.err.println("[DatabaseSeeder] Attempt " + attempt + "/" + MAX_ATTEMPTS
                        + " failed: " + e.getMessage());
                if (attempt < MAX_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        System.err.println("[DatabaseSeeder] WARNING: Could not seed demo data after "
                + MAX_ATTEMPTS + " attempts. Demo logins may fail until the database is reachable.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // no-op
    }

    /**
     * Idempotent entry point used at startup and before demo credential logins.
     * Safe to call concurrently; only one thread performs the seed work at a time.
     */
    public static void ensureDemoWorkspace() throws DatabaseException {
        if (seedCompleted) {
            // Still verify accounts exist (e.g. DB wiped while app stayed up)
            UserDAO userDAO = new UserDAO();
            if (userDAO.findByEmail(DEMO_ADMIN_EMAIL).isPresent()
                    && userDAO.findByEmail(DEMO_USER_EMAIL).isPresent()) {
                return;
            }
            seedCompleted = false;
        }

        synchronized (SEED_LOCK) {
            if (seedCompleted) {
                UserDAO userDAO = new UserDAO();
                if (userDAO.findByEmail(DEMO_ADMIN_EMAIL).isPresent()
                        && userDAO.findByEmail(DEMO_USER_EMAIL).isPresent()) {
                    return;
                }
            }

            DatabaseSeeder seeder = new DatabaseSeeder();
            seeder.seedAll();
            seedCompleted = true;
        }
    }

    /**
     * Returns true when the email is a known demo persona that should trigger
     * auto-seed before authentication.
     */
    public static boolean isDemoEmail(String email) {
        if (email == null) {
            return false;
        }
        String cleaned = email.trim().toLowerCase();
        return DEMO_ADMIN_EMAIL.equals(cleaned) || DEMO_USER_EMAIL.equals(cleaned);
    }

    private void seedAll() throws DatabaseException {
        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        ExpenseDAO expenseDAO = new ExpenseDAO();
        BudgetDAO budgetDAO = new BudgetDAO();
        GoalDAO goalDAO = new GoalDAO();
        SecurityLogDAO securityLogDAO = new SecurityLogDAO();
        SystemSettingDAO settingDAO = new SystemSettingDAO();

        User admin = ensureUser(userDAO,
                "System Administrator",
                DEMO_ADMIN_EMAIL,
                ADMIN_HASH,
                "ADMIN",
                "Salaried Employee");

        User demoUser = ensureUser(userDAO,
                "Arjun Sharma",
                DEMO_USER_EMAIL,
                USER_HASH,
                "USER",
                "Salaried Employee");

        // Extra sample accounts so Admin → User Management looks populated
        ensureUser(userDAO,
                "Priya Patel",
                "priya.patel@example.com",
                PasswordUtil.hashPassword("User@123"),
                "USER",
                "Self-Employed");
        ensureUser(userDAO,
                "Rahul Verma",
                "rahul.verma@example.com",
                PasswordUtil.hashPassword("User@123"),
                "USER",
                "Student");

        Map<String, Integer> categoryIds = ensureCategories(categoryDAO);
        seedUserMockData(demoUser.getId(), categoryIds, expenseDAO, budgetDAO, goalDAO);
        seedAdminMockData(admin.getId(), demoUser.getId(), securityLogDAO, settingDAO);
    }

    private User ensureUser(UserDAO userDAO, String name, String email, String passwordHash,
                            String role, String occupation) throws DatabaseException {
        Optional<User> existing = userDAO.findByEmail(email);
        if (existing.isPresent()) {
            System.out.println("[DatabaseSeeder] Already present: " + email);
            return existing.get();
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setStatus("ACTIVE");
        user.setOccupation(occupation);

        if (userDAO.save(user)) {
            System.out.println("[DatabaseSeeder] Created demo " + role + ": " + email);
            return user;
        }
        throw new DatabaseException("Failed to insert demo user: " + email);
    }

    private Map<String, Integer> ensureCategories(CategoryDAO categoryDAO) throws DatabaseException {
        // Keys used by mock expense/budget seeding (Food, Bills, Shopping, Travel + extras)
        String[][] defaults = {
                {"Food & Dining", "EXPENSE"},
                {"Rent & Housing", "EXPENSE"},
                {"Utilities & Bills", "EXPENSE"},
                {"Transportation", "EXPENSE"},
                {"Entertainment & Leisure", "EXPENSE"},
                {"Healthcare & Medical", "EXPENSE"},
                {"Shopping & Groceries", "EXPENSE"},
                {"Travel", "EXPENSE"},
                {"Salary & Freelancing", "INCOME"}
        };

        Map<String, Integer> ids = new LinkedHashMap<>();
        for (String[] row : defaults) {
            String name = row[0];
            String type = row[1];
            Optional<Category> existing = categoryDAO.findByName(name);
            if (existing.isPresent()) {
                ids.put(name, existing.get().getId());
            } else {
                Category category = new Category();
                category.setName(name);
                category.setType(type);
                if (!categoryDAO.save(category)) {
                    throw new DatabaseException("Failed to insert category: " + name);
                }
                ids.put(name, category.getId());
                System.out.println("[DatabaseSeeder] Created category: " + name);
            }
        }
        return ids;
    }

    private void seedUserMockData(int userId, Map<String, Integer> categoryIds,
                                  ExpenseDAO expenseDAO, BudgetDAO budgetDAO, GoalDAO goalDAO)
            throws DatabaseException {

        int expenseCount = expenseDAO.countByUserId(userId, null, null, null);
        if (expenseCount == 0) {
            seedExpenses(userId, categoryIds, expenseDAO);
            System.out.println("[DatabaseSeeder] Seeded INR expenses for user@" + userId);
        } else {
            System.out.println("[DatabaseSeeder] Expenses already present for user@" + userId
                    + " (" + expenseCount + ")");
        }

        List<Budget> budgets = budgetDAO.findByUserId(userId);
        if (budgets.isEmpty()) {
            seedBudgets(userId, categoryIds, budgetDAO);
            System.out.println("[DatabaseSeeder] Seeded budget limits for user@" + userId);
        }

        List<Goal> goals = goalDAO.findByUserId(userId);
        if (goals.isEmpty()) {
            seedGoals(userId, goalDAO);
            System.out.println("[DatabaseSeeder] Seeded savings goals for user@" + userId);
        }
    }

    private void seedExpenses(int userId, Map<String, Integer> cats, ExpenseDAO expenseDAO)
            throws DatabaseException {
        int food = cats.get("Food & Dining");
        int bills = cats.get("Utilities & Bills");
        int shopping = cats.get("Shopping & Groceries");
        int travel = cats.containsKey("Travel") ? cats.get("Travel") : cats.get("Transportation");
        int rent = cats.get("Rent & Housing");
        int transport = cats.get("Transportation");
        int entertainment = cats.get("Entertainment & Leisure");
        int healthcare = cats.get("Healthcare & Medical");

        LocalDate today = LocalDate.now();

        // Realistic INR transactions across Food / Bills / Shopping / Travel (+ supporting categories)
        // Spread over ~90 days so monthly trend charts are fully populated.
        Object[][] rows = {
                // Food
                {food, 450.50, 1, "Swiggy lunch — paneer wrap", PaymentMode.UPI},
                {food, 289.00, 3, "Café coffee & croissant", PaymentMode.UPI},
                {food, 1250.00, 6, "Weekend dinner at Barbeque Nation", PaymentMode.CREDIT_CARD},
                {food, 523.00, 12, "BigBasket groceries top-up", PaymentMode.WALLET},
                {food, 642.00, 18, "Office team lunch", PaymentMode.UPI},
                {food, 380.00, 25, "Domino's pizza night", PaymentMode.UPI},
                {food, 910.00, 35, "Monthly grocery haul — Nature's Basket", PaymentMode.DEBIT_CARD},
                {food, 275.00, 48, "Street-food chaat & juice", PaymentMode.CASH},
                {food, 1100.00, 62, "Anniversary dinner", PaymentMode.CREDIT_CARD},
                {food, 495.00, 75, "Lunch thali near office", PaymentMode.UPI},
                // Bills
                {bills, 850.00, 2, "Electricity bill — BESCOM", PaymentMode.NET_BANKING},
                {bills, 599.00, 8, "Airtel broadband fibre", PaymentMode.UPI},
                {bills, 399.00, 14, "Jio postpaid mobile", PaymentMode.UPI},
                {bills, 921.50, 28, "Water + society maintenance", PaymentMode.NET_BANKING},
                {bills, 1499.00, 40, "Netflix + Spotify annual share", PaymentMode.CREDIT_CARD},
                {bills, 750.00, 55, "LPG cylinder refill", PaymentMode.UPI},
                {bills, 2100.00, 70, "Quarterly electricity settlement", PaymentMode.NET_BANKING},
                // Shopping
                {shopping, 1207.75, 4, "Amazon — headphones & cables", PaymentMode.CREDIT_CARD},
                {shopping, 784.00, 11, "Myntra festive kurta", PaymentMode.CREDIT_CARD},
                {shopping, 2499.00, 20, "Decathlon running shoes", PaymentMode.DEBIT_CARD},
                {shopping, 650.00, 33, "IKEA desk organiser", PaymentMode.UPI},
                {shopping, 1899.00, 45, "Flipkart home appliances sale", PaymentMode.CREDIT_CARD},
                {shopping, 420.00, 58, "Stationery & printer paper", PaymentMode.CASH},
                {shopping, 3200.00, 80, "Nike hoodie — offline store", PaymentMode.DEBIT_CARD},
                // Travel
                {travel, 3200.00, 5, "IRCTC Mumbai weekend tickets", PaymentMode.NET_BANKING},
                {travel, 1850.00, 16, "Uber / Ola airport transfers", PaymentMode.UPI},
                {travel, 8900.00, 30, "Indigo flight BLR → GOI", PaymentMode.CREDIT_CARD},
                {travel, 4500.00, 42, "Goa hotel advance — Booking.com", PaymentMode.CREDIT_CARD},
                {travel, 680.00, 52, "Local sightseeing cab day pass", PaymentMode.CASH},
                {travel, 2100.00, 68, "MakeMyTrip bus + snacks", PaymentMode.UPI},
                {travel, 5500.00, 85, "Family trip hotel deposit", PaymentMode.NET_BANKING},
                // Supporting categories for a fuller dashboard
                {rent, 18500.00, 15, "Monthly apartment rent", PaymentMode.NET_BANKING},
                {rent, 18500.00, 45, "Monthly apartment rent", PaymentMode.NET_BANKING},
                {rent, 18500.00, 75, "Monthly apartment rent", PaymentMode.NET_BANKING},
                {transport, 320.00, 7, "Metro card recharge", PaymentMode.DEBIT_CARD},
                {transport, 180.00, 22, "Auto rickshaw to client site", PaymentMode.CASH},
                {transport, 400.00, 50, "Petrol top-up", PaymentMode.DEBIT_CARD},
                {entertainment, 450.00, 9, "PVR movie night", PaymentMode.UPI},
                {entertainment, 1200.00, 38, "Concert tickets — Indiranagar", PaymentMode.CREDIT_CARD},
                {entertainment, 650.00, 72, "Board-game café evening", PaymentMode.UPI},
                {healthcare, 650.00, 13, "Apollo pharmacy — medicines", PaymentMode.DEBIT_CARD},
                {healthcare, 1500.00, 60, "Annual health checkup", PaymentMode.NET_BANKING}
        };

        for (Object[] row : rows) {
            Expense expense = new Expense();
            expense.setUserId(userId);
            expense.setCategoryId((Integer) row[0]);
            expense.setAmount(((Number) row[1]).doubleValue());
            expense.setDate(today.minusDays(((Number) row[2]).longValue()));
            expense.setNote((String) row[3]);
            expense.setPaymentMode((PaymentMode) row[4]);
            if (!expenseDAO.save(expense)) {
                throw new DatabaseException("Failed to seed expense: " + row[3]);
            }
        }
    }

    private void seedBudgets(int userId, Map<String, Integer> cats, BudgetDAO budgetDAO)
            throws DatabaseException {
        LocalDate start = YearMonth.now().atDay(1);
        LocalDate end = YearMonth.now().atEndOfMonth();

        int travelCat = cats.containsKey("Travel") ? cats.get("Travel") : cats.get("Transportation");

        // Category budget limits in INR for the current month
        Object[][] rows = {
                {cats.get("Food & Dining"), 8000.00},
                {cats.get("Utilities & Bills"), 5000.00},
                {cats.get("Shopping & Groceries"), 6000.00},
                {travelCat, 15000.00},
                {cats.get("Transportation"), 2500.00},
                {cats.get("Entertainment & Leisure"), 3000.00}
        };

        for (Object[] row : rows) {
            Budget budget = new Budget();
            budget.setUserId(userId);
            budget.setCategoryId((Integer) row[0]);
            budget.setAmount(((Number) row[1]).doubleValue());
            budget.setStartDate(start);
            budget.setEndDate(end);
            if (!budgetDAO.save(budget)) {
                throw new DatabaseException("Failed to seed budget for category " + row[0]);
            }
        }
    }

    private void seedGoals(int userId, GoalDAO goalDAO) throws DatabaseException {
        LocalDate today = LocalDate.now();

        Object[][] rows = {
                {"Emergency Fund (6 Months)", 300000.00, 185000.00, today.plusMonths(6), "IN_PROGRESS"},
                {"New MacBook Pro M4", 180000.00, 145000.00, today.plusMonths(3), "IN_PROGRESS"},
                {"Goa Vacation Trip", 50000.00, 50000.00, today.minusMonths(1), "COMPLETED"},
                {"Diwali Shopping Buffer", 25000.00, 12000.00, today.plusMonths(1), "IN_PROGRESS"}
        };

        for (Object[] row : rows) {
            Goal goal = new Goal();
            goal.setUserId(userId);
            goal.setDescription((String) row[0]);
            goal.setTargetAmount(((Number) row[1]).doubleValue());
            goal.setSavedAmount(((Number) row[2]).doubleValue());
            goal.setDeadline((LocalDate) row[3]);
            goal.setStatus((String) row[4]);
            if (!goalDAO.save(goal)) {
                throw new DatabaseException("Failed to seed goal: " + row[0]);
            }
        }
    }

    private void seedAdminMockData(int adminId, int demoUserId,
                                   SecurityLogDAO securityLogDAO,
                                   SystemSettingDAO settingDAO) throws DatabaseException {
        if (securityLogDAO.countLogs() == 0) {
            seedSecurityLogs(adminId, demoUserId, securityLogDAO);
            System.out.println("[DatabaseSeeder] Seeded system security logs");
        }

        seedSystemSettings(adminId, settingDAO);
    }

    private void seedSecurityLogs(int adminId, int demoUserId, SecurityLogDAO dao)
            throws DatabaseException {
        Object[][] rows = {
                {adminId, "Admin logged into system", "127.0.0.1"},
                {demoUserId, "User logged into system", "127.0.0.1"},
                {demoUserId, "Expense record created (demo seed)", "127.0.0.1"},
                {adminId, "Viewed security audit trail", "10.0.0.12"},
                {adminId, "Updated system setting: platform_currency", "10.0.0.12"},
                {demoUserId, "Budget limit created for Food & Dining", "192.168.1.45"},
                {null, "Failed login (user not found): unknown@example.com", "103.21.244.8"},
                {demoUserId, "User logged in successfully as USER", "192.168.1.45"},
                {adminId, "User management: listed all accounts", "10.0.0.12"},
                {adminId, "Admin logged in successfully as ADMIN", "10.0.0.12"}
        };

        for (Object[] row : rows) {
            Integer userId = (Integer) row[0];
            dao.saveLog(userId, (String) row[1], (String) row[2]);
        }
    }

    private void seedSystemSettings(int adminId, SystemSettingDAO settingDAO)
            throws DatabaseException {
        String[][] defaults = {
                {"allow_registration", "true"},
                {"max_login_attempts", "5"},
                {"session_timeout_minutes", "15"},
                {"maintenance_mode", "false"},
                {"platform_currency", "INR"},
                {"app_name", "FinanceFlow"},
                {"date_format", "dd-MM-yyyy"},
                {"items_per_page", "10"},
                {"default_theme", "light"},
                {"encrypt_expense_notes", "true"},
                {"mask_account_details", "false"},
                {"bcrypt_rounds", "10"},
                {"lockout_duration_minutes", "15"},
                {"require_strong_passwords", "true"},
                {"confirm_on_add", "true"},
                {"confirm_on_update", "true"},
                {"confirm_on_delete", "true"},
                {"prompt_before_delete", "true"}
        };

        int inserted = 0;
        for (String[] pair : defaults) {
            if (settingDAO.findByKey(pair[0]).isEmpty()) {
                settingDAO.setSettingValue(pair[0], pair[1], adminId);
                inserted++;
            }
        }
        if (inserted > 0) {
            System.out.println("[DatabaseSeeder] Seeded " + inserted + " system settings");
        } else {
            System.out.println("[DatabaseSeeder] System settings already present");
        }
    }
}
