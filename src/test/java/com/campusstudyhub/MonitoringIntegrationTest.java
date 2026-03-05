package com.campusstudyhub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
        "management.endpoint.prometheus.enabled=true",
        "management.prometheus.metrics.export.enabled=true",
        "management.endpoint.health.show-details=always",
        "spring.datasource.url=jdbc:h2:mem:monitoringdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@ActiveProfiles("test")
class MonitoringIntegrationTest {

    @LocalServerPort
    private int port;

    @Value("${management.endpoints.web.base-path:/actuator}")
    private String actuatorPath;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void testHealthEndpoint() {
        String url = "http://localhost:" + port + actuatorPath + "/health";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody().get("status"));
        assertTrue(response.getBody().containsKey("components"), "Health details should be visible (always)");
    }

    @Test
    void testPrometheusEndpoint() {
        String url = "http://localhost:" + port + actuatorPath + "/prometheus";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        if (body == null || !body.contains("jvm_memory_used_bytes")) {
            System.err.println("Prometheus Response Body: " + body);
        }
        assertTrue(body != null && body.contains("jvm_memory_used_bytes"), "Prometheus metrics should be present");
    }
}
