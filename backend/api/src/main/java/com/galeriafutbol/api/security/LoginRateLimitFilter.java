package com.galeriafutbol.api.security;

import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/auth/login";

    private final LoginRateLimiterService loginRateLimiterService;

    public LoginRateLimitFilter(LoginRateLimiterService loginRateLimiterService) {
        this.loginRateLimiterService = loginRateLimiterService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod()) || !LOGIN_PATH.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientKey = resolveClientKey(request);

        if (loginRateLimiterService.isBlocked(clientKey)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter()
                    .write("{\"message\":\"Demasiados intentos de inicio de sesión. Intenta nuevamente más tarde.\"}");
            return;
        }

        // Register pessimistically before the chain — Spring MVC exception handlers
        // commit the response before returning, making response.getStatus() unreliable
        // after doFilter. We clear on confirmed success instead.
        loginRateLimiterService.registerFailure(clientKey);

        filterChain.doFilter(request, response);

        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            loginRateLimiterService.clearAttempts(clientKey);
        }
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor == null || forwardedFor.isBlank()) {
            return request.getRemoteAddr();
        }

        return forwardedFor.split(",")[0].trim();
    }
}