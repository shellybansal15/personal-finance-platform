package com.finance.util;

import com.finance.dao.UserDAO;
import com.finance.exception.DatabaseException;
import com.finance.model.User;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Ensures demo login accounts exist whenever the application starts.
 * Idempotent: skips emails that are already present so restarts never duplicate rows.
 * Helps cloud hosts (e.g. Render) where the DB may come up empty after a redeploy.
 */
@WebListener
public class DatabaseSeeder implements ServletContextListener {

    private static final int MAX_ATTEMPTS = 5;
    private static final long RETRY_DELAY_MS = 2000L;

    /** Pre-computed BCrypt hashes matching README / seed.sql demo passwords. */
    private static final String ADMIN_HASH =
            "$2a$10$TeuGs1SqL6BgDJ3tVKUXpuqsNcNZGKSLS6OUTqBTH9JkNnEPYonci"; // Admin@123
    private static final String USER_HASH =
            "$2a$10$.Eq/WSEzxYEeLi2aGtIH6eYWyxhMD3QHQme1U.m4gGLzkXypBYwBy"; // User@123

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[DatabaseSeeder] Ensuring demo accounts exist…");
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                seedDemoAccounts();
                System.out.println("[DatabaseSeeder] Demo account check complete.");
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
        System.err.println("[DatabaseSeeder] WARNING: Could not seed demo accounts after "
                + MAX_ATTEMPTS + " attempts. Demo logins may fail until the database is reachable.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // no-op
    }

    private void seedDemoAccounts() throws DatabaseException {
        UserDAO userDAO = new UserDAO();

        ensureUser(userDAO,
                "System Administrator",
                "admin@finance.com",
                ADMIN_HASH,
                "ADMIN",
                "Salaried Employee");

        ensureUser(userDAO,
                "Arjun Sharma",
                "user@finance.com",
                USER_HASH,
                "USER",
                "Salaried Employee");
    }

    private void ensureUser(UserDAO userDAO, String name, String email, String passwordHash,
                            String role, String occupation) throws DatabaseException {
        if (userDAO.findByEmail(email).isPresent()) {
            System.out.println("[DatabaseSeeder] Already present: " + email);
            return;
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
        } else {
            throw new DatabaseException("Failed to insert demo user: " + email);
        }
    }
}
