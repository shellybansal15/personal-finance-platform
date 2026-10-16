package com.finance.util;

import com.finance.model.ActiveSession;
import com.finance.model.User;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks authenticated HttpSessions for the admin Security module.
 * Registered as a listener so destroyed/expired sessions are removed automatically.
 */
@WebListener
public class SessionTracker implements HttpSessionListener {

    private static final ConcurrentHashMap<String, TrackedSession> ACTIVE = new ConcurrentHashMap<>();

    private static final class TrackedSession {
        final HttpSession session;
        final ActiveSession snapshot;

        TrackedSession(HttpSession session, ActiveSession snapshot) {
            this.session = session;
            this.snapshot = snapshot;
        }
    }

    /**
     * Registers an authenticated session after successful login.
     */
    public static void register(HttpSession session, User user, String ipAddress) {
        if (session == null || user == null) {
            return;
        }
        LocalDateTime loginTime = LocalDateTime.now();
        session.setAttribute("clientIp", ipAddress != null ? ipAddress : "127.0.0.1");
        session.setAttribute("loginTime", loginTime);

        ActiveSession info = new ActiveSession(
                session.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                ipAddress != null ? ipAddress : "127.0.0.1",
                loginTime,
                session.getLastAccessedTime(),
                session.getMaxInactiveInterval()
        );
        ACTIVE.put(session.getId(), new TrackedSession(session, info));
    }

    public static void unregister(String sessionId) {
        if (sessionId != null) {
            ACTIVE.remove(sessionId);
        }
    }

    /**
     * Live view of authenticated sessions (last-access refreshed from the HttpSession).
     */
    public static List<ActiveSession> getActiveSessions() {
        List<ActiveSession> result = new ArrayList<>();
        for (TrackedSession tracked : ACTIVE.values()) {
            try {
                HttpSession s = tracked.session;
                ActiveSession copy = new ActiveSession(
                        tracked.snapshot.getSessionId(),
                        tracked.snapshot.getUserId(),
                        tracked.snapshot.getUserName(),
                        tracked.snapshot.getUserEmail(),
                        tracked.snapshot.getUserRole(),
                        tracked.snapshot.getIpAddress(),
                        tracked.snapshot.getLoginTime(),
                        s.getLastAccessedTime(),
                        s.getMaxInactiveInterval()
                );
                result.add(copy);
            } catch (IllegalStateException expired) {
                // Session already invalidated — drop it
                ACTIVE.remove(tracked.snapshot.getSessionId());
            }
        }
        result.sort(Comparator.comparing(ActiveSession::getLastAccessedTimeMs).reversed());
        return result;
    }

    public static int countActiveSessions() {
        return ACTIVE.size();
    }

    /**
     * Invalidates all live sessions for a user (e.g. after delete/block).
     * Optionally keeps the acting admin's own session intact.
     *
     * @return number of sessions invalidated
     */
    public static int invalidateUserSessions(int userId, String exceptSessionId) {
        int invalidated = 0;
        List<String> toRemove = new ArrayList<>();
        for (TrackedSession tracked : ACTIVE.values()) {
            if (tracked.snapshot.getUserId() == userId) {
                String sid = tracked.snapshot.getSessionId();
                if (exceptSessionId != null && exceptSessionId.equals(sid)) {
                    continue;
                }
                toRemove.add(sid);
                try {
                    tracked.session.invalidate();
                    invalidated++;
                } catch (IllegalStateException ignored) {
                    // already gone
                }
            }
        }
        for (String sid : toRemove) {
            ACTIVE.remove(sid);
        }
        return invalidated;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Registered only after successful authentication via register()
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ACTIVE.remove(se.getSession().getId());
    }
}
