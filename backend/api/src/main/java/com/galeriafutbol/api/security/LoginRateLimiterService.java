package com.galeriafutbol.api.security;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginRateLimiterService {

    private final int maxAttempts;
    private final long windowMillis;
    private final long blockMillis;
    private final Map<String, AttemptWindow> attemptsByKey = new ConcurrentHashMap<>();

    public LoginRateLimiterService(
            @Value("${security.rate-limit.login.max-attempts:5}") int maxAttempts,
            @Value("${security.rate-limit.login.window-seconds:60}") long windowSeconds,
            @Value("${security.rate-limit.login.block-seconds:300}") long blockSeconds) {
        this.maxAttempts = maxAttempts;
        this.windowMillis = Math.max(windowSeconds, 1) * 1000;
        this.blockMillis = Math.max(blockSeconds, 1) * 1000;
    }

    public boolean isBlocked(String key) {
        long now = System.currentTimeMillis();
        AttemptWindow attemptWindow = attemptsByKey.computeIfAbsent(key, ignored -> new AttemptWindow());

        synchronized (attemptWindow) {
            purgeExpiredAttempts(attemptWindow, now);

            if (attemptWindow.blockedUntil > now) {
                return true;
            }

            if (attemptWindow.attempts.size() >= maxAttempts) {
                attemptWindow.blockedUntil = now + blockMillis;
                attemptWindow.attempts.clear();
                return true;
            }

            return false;
        }
    }

    public void registerFailure(String key) {
        long now = System.currentTimeMillis();
        AttemptWindow attemptWindow = attemptsByKey.computeIfAbsent(key, ignored -> new AttemptWindow());

        synchronized (attemptWindow) {
            purgeExpiredAttempts(attemptWindow, now);

            if (attemptWindow.blockedUntil > now) {
                return;
            }

            attemptWindow.attempts.addLast(now);

            if (attemptWindow.attempts.size() >= maxAttempts) {
                attemptWindow.blockedUntil = now + blockMillis;
                attemptWindow.attempts.clear();
            }
        }
    }

    public void clearAttempts(String key) {
        attemptsByKey.remove(key);
    }

    private void purgeExpiredAttempts(AttemptWindow attemptWindow, long now) {
        long minAllowedTs = now - windowMillis;
        while (!attemptWindow.attempts.isEmpty() && attemptWindow.attempts.peekFirst() < minAllowedTs) {
            attemptWindow.attempts.pollFirst();
        }
        if (attemptWindow.blockedUntil <= now && attemptWindow.attempts.isEmpty()) {
            attemptWindow.blockedUntil = 0;
        }
    }

    private static class AttemptWindow {
        private final Deque<Long> attempts = new ArrayDeque<>();
        private long blockedUntil;
    }
}