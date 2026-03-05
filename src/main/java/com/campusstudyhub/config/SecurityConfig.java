package com.campusstudyhub.config;

import com.campusstudyhub.entity.User;
import com.campusstudyhub.repository.UserRepository;
import com.campusstudyhub.security.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import com.campusstudyhub.security.TenantFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Security configuration for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final UserRepository userRepository;
        private final RateLimitingFilter rateLimitingFilter;
        private final TenantFilter tenantFilter;

        @Value("${app.frontend.url:http://localhost:3000}")
        private String frontendUrl;

        public SecurityConfig(UserRepository userRepository, RateLimitingFilter rateLimitingFilter,
                        TenantFilter tenantFilter) {
                this.userRepository = userRepository;
                this.rateLimitingFilter = rateLimitingFilter;
                this.tenantFilter = tenantFilter;
        }

        /**
         * BCrypt password encoder with strength 10.
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(10);
        }

        /**
         * Custom UserDetailsService that loads users from the database.
         */
        @Bean
        public UserDetailsService userDetailsService() {
                return username -> {
                        User user = userRepository.findByEmail(username)
                                        .orElseThrow(() -> new UsernameNotFoundException(
                                                        "User not found: " + username));

                        return new org.springframework.security.core.userdetails.User(
                                        user.getEmail(),
                                        user.getPassword(),
                                        Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
                };
        }

        /**
         * CORS configuration to allow frontend and mobile origins.
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList(frontendUrl, "http://localhost:8080"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        /**
         * Security filter chain configuration.
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CORS
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // CSRF — disable for REST API endpoints (they use JSON, not forms)
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**"))

                                // Authorization rules
                                .authorizeHttpRequests(auth -> auth
                                                // Public resources
                                                .requestMatchers("/", "/login", "/register", "/error/**").permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/actuator/**").permitAll()

                                                // Admin only — Thymeleaf admin pages
                                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                                // Admin API endpoints
                                                .requestMatchers("/api/v1/bookings/*/approve",
                                                                "/api/v1/bookings/*/reject")
                                                .hasRole("ADMIN")

                                                // Authenticated API endpoints
                                                .requestMatchers("/api/v1/**").authenticated()

                                                // Authenticated Thymeleaf pages
                                                .requestMatchers("/bookings/**").authenticated()
                                                .requestMatchers("/study-planner/**").authenticated()
                                                .requestMatchers("/map", "/vr-walkthrough").authenticated()
                                                .requestMatchers("/files/**").authenticated()
                                                .requestMatchers("/dashboard", "/semesters/**", "/subjects/**",
                                                                "/search")
                                                .authenticated()

                                                // All other requests require authentication
                                                .anyRequest().authenticated())

                                // Form login configuration
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())

                                // Logout configuration
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())

                                // Exception handling
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/error/403"))

                                // Security Headers
                                .headers(headers -> {
                                        headers.contentSecurityPolicy(csp -> csp
                                                        .policyDirectives(
                                                                        "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; font-src 'self' https://cdn.jsdelivr.net; img-src 'self' data: https:; frame-src 'self' https://www.youtube.com;"));
                                        headers.referrerPolicy(referrer -> referrer
                                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN));
                                        headers.permissionsPolicy(permissions -> permissions
                                                        .policy("geolocation=(), microphone=(), camera=()"));
                                        headers.httpStrictTransportSecurity(hsts -> hsts
                                                        .includeSubDomains(true)
                                                        .maxAgeInSeconds(31536000)
                                                        .requestMatcher(AnyRequestMatcher.INSTANCE));
                                });

                // Add Multi-tenant and Rate Limiting Filters
                http.addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class);
                http.addFilterAfter(rateLimitingFilter, TenantFilter.class);

                return http.build();
        }
}
