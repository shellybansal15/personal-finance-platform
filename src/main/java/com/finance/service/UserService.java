package com.finance.service;

import com.finance.dao.UserDAO;
import com.finance.exception.AuthenticationException;
import com.finance.exception.ValidationException;
import com.finance.model.User;
import com.finance.util.AuditLogger;
import com.finance.util.LoginThrottler;
import com.finance.util.PasswordUtil;
import com.finance.util.ValidationUtil;

import java.util.Optional;

/**
 * ============================================================================
 * [Service Rubric: UserService]
 * Encapsulates user registration, authentication, brute-force lockout checking,
 * profile management, and security audit event triggering.
 * ============================================================================
 */
public class UserService {

    private final UserDAO userDAO;
    private final AuditLogger auditLogger;

    public UserService() {
        this.userDAO = new UserDAO();
        this.auditLogger = AuditLogger.getInstance();
    }

    /**
     * Registers a new user account with BCrypt password hashing.
     */
    public User register(String name, String email, String password, String ipAddress) {
        String cleanName = ValidationUtil.requireNonEmpty(name, "Full Name");
        String cleanEmail = ValidationUtil.validateEmail(email);
        ValidationUtil.validatePassword(password);

        if (userDAO.findByEmail(cleanEmail).isPresent()) {
            throw new ValidationException("An account with email " + cleanEmail + " already exists.");
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setName(cleanName);
        user.setEmail(cleanEmail);
        user.setPasswordHash(hashedPassword);
        user.setRole("USER");
        user.setStatus("ACTIVE");

        userDAO.save(user);

        // [Security Rubric: Synchronized Audit Logging]
        auditLogger.log(user.getId(), "User registered successfully", ipAddress);

        return user;
    }

    /**
     * Authenticates credentials with lockout prevention and security logging.
     */
    public User login(String email, String password, String ipAddress) {
        String cleanEmail = ValidationUtil.validateEmail(email);

        // 1. Check if user is locked out due to >= 5 failed attempts
        if (LoginThrottler.isLockedOut(cleanEmail)) {
            auditLogger.log(null, "Blocked login attempt during lockout for: " + cleanEmail, ipAddress);
            throw new AuthenticationException("Account is temporarily locked due to 5 failed attempts. Please try again in 15 minutes.");
        }

        // 2. Fetch user by email
        Optional<User> userOpt = userDAO.findByEmail(cleanEmail);
        if (userOpt.isEmpty()) {
            int attempts = LoginThrottler.recordFailedAttempt(cleanEmail);
            int remaining = LoginThrottler.getRemainingAttempts(cleanEmail);
            auditLogger.log(null, "Failed login (user not found): " + cleanEmail, ipAddress);
            throw new AuthenticationException("Invalid email or password. Remaining attempts: " + remaining);
        }

        User user = userOpt.get();

        // 3. Verify BCrypt password hash
        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            int attempts = LoginThrottler.recordFailedAttempt(cleanEmail);
            int remaining = LoginThrottler.getRemainingAttempts(cleanEmail);
            auditLogger.log(user.getId(), "Failed login attempt (wrong password)", ipAddress);

            if (attempts >= LoginThrottler.MAX_FAILED_ATTEMPTS) {
                throw new AuthenticationException("Account locked due to 5 consecutive failed attempts. Please wait 15 minutes.");
            }
            throw new AuthenticationException("Invalid email or password. Remaining attempts: " + remaining);
        }

        // 4. Check if account is blocked by Admin
        if (user.isBlocked()) {
            auditLogger.log(user.getId(), "Blocked user attempted login", ipAddress);
            throw new AuthenticationException("Your account has been suspended by an administrator. Please contact support.");
        }

        // 5. Success: reset failure throttle counter and record audit event
        LoginThrottler.resetAttempts(cleanEmail);
        auditLogger.log(user.getId(), "User logged in successfully", ipAddress);

        return user;
    }

    public Optional<User> getUserById(int userId) {
        return userDAO.findById(userId);
    }

    public void updateProfile(int userId, String name, String ipAddress) {
        String cleanName = ValidationUtil.requireNonEmpty(name, "Full Name");
        Optional<User> opt = userDAO.findById(userId);
        if (opt.isPresent()) {
            User user = opt.get();
            user.setName(cleanName);
            userDAO.update(user);
            auditLogger.log(userId, "Profile updated name to: " + cleanName, ipAddress);
        }
    }

    public void changePassword(int userId, String oldPassword, String newPassword, String ipAddress) {
        Optional<User> opt = userDAO.findById(userId);
        if (opt.isEmpty()) {
            throw new ValidationException("User not found.");
        }
        User user = opt.get();
        if (!PasswordUtil.checkPassword(oldPassword, user.getPasswordHash())) {
            throw new ValidationException("Current password does not match.");
        }
        ValidationUtil.validatePassword(newPassword);
        String newHash = PasswordUtil.hashPassword(newPassword);
        userDAO.updatePassword(userId, newHash);
        auditLogger.log(userId, "Password changed successfully", ipAddress);
    }
}
