package com.campusstudyhub.security;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filter for rate limiting based on IP address and request path.
 * Uses Bucket4j for token bucket algorithm.
 */
@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String ip = getClientIP(httpRequest);
        String path = httpRequest.getRequestURI();
        String key = ip + ":" + getPathGroup(path);

        Bucket bucket = buckets.computeIfAbsent(key, k -> createNewBucket(path));

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{ \"error\": \"Too many requests. Please try again later.\" }");
        }
    }

    private Bucket createNewBucket(String path) {
        if (path.startsWith("/login") || path.startsWith("/register")) {
            // Strict limit for auth endpoints: 10 per minute
            return Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(10)
                            .refillIntervally(10, Duration.ofMinutes(1))
                            .build())
                    .build();
        } else if (path.startsWith("/api/")) {
            // API endpoints: 100 per minute
            return Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(100)
                            .refillIntervally(100, Duration.ofMinutes(1))
                            .build())
                    .build();
        } else {
            // General pages: 200 per minute
            return Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(200)
                            .refillIntervally(200, Duration.ofMinutes(1))
                            .build())
                    .build();
        }
    }

    private String getPathGroup(String path) {
        if (path.startsWith("/login"))
            return "auth";
        if (path.startsWith("/register"))
            return "auth";
        if (path.startsWith("/api/"))
            return "api";
        return "general";
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    public void reset() {
        buckets.clear();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
