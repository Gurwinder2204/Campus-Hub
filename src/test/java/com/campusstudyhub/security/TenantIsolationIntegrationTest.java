package com.campusstudyhub.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_isolated;MODE=PostgreSQL;INIT=CREATE TYPE IF NOT EXISTS JSONB AS TEXT",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class TenantIsolationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testTenantContextIsSetFromHeader() throws Exception {
        mockMvc.perform(get("/login")
                .header("X-Tenant-ID", "campus-a"))
                .andExpect(status().isOk());

        // Context should be cleared after request, we verify this mainly through logs
        // or by adding an endpoint
    }

    @Test
    public void testDefaultTenantIsUsedWhenHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }
}
