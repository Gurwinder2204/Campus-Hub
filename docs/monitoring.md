# Monitoring and Metrics

This project uses Spring Boot Actuator and Micrometer to provide production-grade monitoring.

## Actuator Endpoints

- **Health Checks**: `/actuator/health` (detailed status)
- **Readiness Probe**: `/actuator/health/readiness`
- **Liveness Probe**: `/actuator/health/liveness`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus` (scraping endpoint)

## Configuration

In `application.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.endpoint.health.probes.enabled=true
```

## Local Monitoring (Prometheus/Grafana)

1. Start the services: `docker-compose up -d`
2. Access metrics: `http://localhost:8080/actuator/prometheus`
3. (Optional) Run a local Prometheus instance pointed at the backend container.

## Key Metrics to Watch

- `http.server.requests`: Throughput and latency per endpoint.
- `jvm.memory.used`: Heap usage.
- `hikaricp.connections`: Database connection pool health.
- `system.cpu.usage`: Application CPU consumption.
