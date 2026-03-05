package com.campusstudyhub.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_shutdown;MODE=PostgreSQL;INIT=CREATE TYPE IF NOT EXISTS JSONB AS TEXT",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class GracefulShutdownConfigurationTest {

    @Value("${server.shutdown:}")
    private String shutdown;

    @Value("${spring.lifecycle.timeout-per-shutdown-phase:}")
    private String shutdownTimeout;

    @Test
    public void testGracefulShutdownPropertiesAreLoaded() {
        assertEquals("graceful", shutdown);
        assertEquals("30s", shutdownTimeout);
    }
}
