package com.campusstudyhub.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;INIT=CREATE TYPE IF NOT EXISTS JSONB AS TEXT",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class SecurityHeaderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void setUp() {
        rateLimitingFilter.reset();
    }

    @Test
    public void testSecurityHeadersArePresent() throws Exception {
        // Use /login because / is restricted and redirects to /login (302)
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Security-Policy"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().exists("Strict-Transport-Security"))
                .andExpect(header().exists("Referrer-Policy"));
    }

    @Test
    public void testRateLimitingOnAuthEndpoints() throws Exception {
        // The limit is 10 per minute for auth endpoints.
        // NOTE: The RateLimitingFilter uses a static map, so state persists between
        // tests.
        // We just need to trigger the limit to verify it works.
        boolean limitReached = false;
        for (int i = 0; i < 20; i++) {
            int status = mockMvc.perform(get("/login")).andReturn().getResponse().getStatus();
            if (status == 429) {
                limitReached = true;
                break;
            }
        }
        assert (limitReached);
    }
}
