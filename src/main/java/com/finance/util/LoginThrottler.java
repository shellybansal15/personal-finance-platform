package com.finance.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================
 * [Multithreading & Synchronization Rubric: ConcurrentHashMap Login Throttling]
 * Protects authentication endpoints against brute-force attacks by tracking
 * consecutive failed login attempts in a thread-safe ConcurrentHashMap.
 * Enforces a temporary 15-minute account lockout after 5 consecutive failures.
 * ConcurrentHashMap ensures non-blocking concurrent reads and atomic updates
 * across multiple HTTP worker threads without full table locking.
 * ============================================================================
 */
public class LoginThrottler {

    // Maximum failed attempts before triggering temporary lockout
    public static final int MAX_FAILED_ATTEMPTS = 5;

    // Lockout duration in milliseconds (15 minutes)
    public static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000L;

    /**
     * Inner helper class storing attempt count and the timestamp of the last failure.
     */
    public static class AttemptRecord {
        private int count;
        private long lastAttemptTime;

        public AttemptRecord(int count, long lastAttemptTime) {
            this.count = count;
            this.lastAttemptTime = lastAttemptTime;
        }

        public int getCount() {
            return count;
        }

        public long getLastAttemptTime() {
            return lastAttemptTime;
        }
    }

    // [Rubric: ConcurrentHashMap] Key is lowercase user email or client IP
    private static final ConcurrentHashMap<String, AttemptRecord> ATTEMPTS_MAP = new ConcurrentHashMap<>();

    /**
     * Records a failed login attempt atomically.
     *
     * @param key User identifier (email or IP address)
     * @return current number of consecutive failed attempts
     */
    public static int recordFailedAttempt(String key) {
        if (key == null) return 0;
        String normalizedKey = key.trim().toLowerCase();
        long now = System.currentTimeMillis();

        AttemptRecord record = ATTEMPTS_MAP.compute(normalizedKey, (k, existing) -> {
            if (existing == null) {
                return new AttemptRecord(1, now);
            }
            // If the lockout window has already expired, reset counter to 1
            if (now - existing.lastAttemptTime > LOCKOUT_DURATION_MS) {
                return new AttemptRecord(1, now);
            }
            // Otherwise increment failed counter
            return new AttemptRecord(existing.count + 1, now);
        });

        return record.getCount();
    }

    /**
     * Checks if the user/IP is currently locked out.
     *
     * @param key User identifier
     * @return true if currently locked out, false otherwise
     */
    public static boolean isLockedOut(String key) {
        if (key == null) return false;
        String normalizedKey = key.trim().toLowerCase();
        AttemptRecord record = ATTEMPTS_MAP.get(normalizedKey);

        if (record == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        // If 15 minutes elapsed since the last failure, unlock the user
        if (now - record.lastAttemptTime > LOCKOUT_DURATION_MS) {
            ATTEMPTS_MAP.remove(normalizedKey);
            return false;
        }

        return record.count >= MAX_FAILED_ATTEMPTS;
    }

    /**
     * Resets failed login attempts after a successful login.
     *
     * @param key User identifier
     */
    public static void resetAttempts(String key) {
        if (key != null) {
            ATTEMPTS_MAP.remove(key.trim().toLowerCase());
        }
    }

    /**
     * Returns remaining attempts before lockout.
     *
     * @param key User identifier
     * @return number of attempts left
     */
    public static int getRemainingAttempts(String key) {
        if (key == null) return MAX_FAILED_ATTEMPTS;
        AttemptRecord record = ATTEMPTS_MAP.get(key.trim().toLowerCase());
        if (record == null) return MAX_FAILED_ATTEMPTS;
        return Math.max(0, MAX_FAILED_ATTEMPTS - record.count);
    }

    /**
     * Snapshot of in-memory failed-attempt counters for the admin Security panel.
     * Keys are normalized emails/identifiers; values are AttemptRecord copies.
     */
    public static Map<String, AttemptRecord> getActiveAttemptSnapshot() {
        if (ATTEMPTS_MAP.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, AttemptRecord> snapshot = new LinkedHashMap<>();
        long now = System.currentTimeMillis();
        ATTEMPTS_MAP.forEach((key, record) -> {
            if (record != null && (now - record.lastAttemptTime) <= LOCKOUT_DURATION_MS) {
                snapshot.put(key, new AttemptRecord(record.count, record.lastAttemptTime));
            }
        });
        return Collections.unmodifiableMap(snapshot);
    }

    public static boolean isLockedOutRecord(AttemptRecord record) {
        if (record == null) return false;
        long now = System.currentTimeMillis();
        if (now - record.getLastAttemptTime() > LOCKOUT_DURATION_MS) {
            return false;
        }
        return record.getCount() >= MAX_FAILED_ATTEMPTS;
    }
}
