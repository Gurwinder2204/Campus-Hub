# Campus Study Hub — Portfolio Showcase

## Project Overview

Campus Study Hub is a full-stack web platform built for university students to access study materials, book rooms, manage campus services, and track study progress. It demonstrates production-grade software engineering with modern technologies, security best practices, and comprehensive monitoring.

## Key Features

### 📚 Smart Study Hub
- Semester-wise organization of notes, question papers, and video tutorials
- PDF upload/download with metadata tracking
- Full-text search across all subjects
- Study task planner with priority management

### 🏢 Campus Services
- Room booking with admin approval workflow
- Campus events management
- Lost & found system with photo upload
- Complaint submission and tracking

### 📊 Analytics & Monitoring
- Real-time usage analytics tracking
- Prometheus metrics with Grafana dashboards
- Spring Actuator health checks
- Redis-cached POI endpoints for performance

### 🔒 Security & Compliance
- Spring Security with role-based access (Admin/Student)
- Rate limiting (Bucket4j — 10 req/min auth, 100 req/min API)
- Security headers: CSP, HSTS, Referrer-Policy, Permissions-Policy
- Multi-tenant architecture (optional per-campus isolation)
- Privacy policy and data retention documentation

## Technology Stack

| Category | Technologies |
| --- | --- |
| **Backend** | Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA |
| **Frontend** | Thymeleaf, Bootstrap 5, Bootstrap Icons |
| **Database** | PostgreSQL 15, Flyway migrations (10 versions) |
| **Cache** | Redis |
| **Monitoring** | Prometheus, Grafana, Micrometer |
| **CI/CD** | GitHub Actions (release + e2e pipelines) |
| **Container** | Docker multi-stage build |
| **Testing** | JUnit 5, Cypress, k6 load testing |
| **Notifications** | Firebase Cloud Messaging |

## Architecture Highlights

- **Layered architecture**: Controller → Service → Repository with clear separation of concerns
- **RESTful API design**: Versioned endpoints (`/api/v1/`) with proper HTTP semantics
- **Database migrations**: 10 Flyway migration scripts for schema evolution
- **Multi-tenant ready**: Thread-local tenant context with header-based identification
- **Graceful shutdown**: 30-second timeout for in-flight requests
- **Production profile**: Externalized secrets, optimized JPA settings

## Security Features

- CSRF protection on all web forms
- Rate limiting to prevent abuse
- Content Security Policy (CSP) headers
- HTTP Strict Transport Security (HSTS)
- Non-root Docker container user
- No secrets in repository (CI secrets + .env.example)

## Performance Optimizations

- Redis caching for frequently accessed POI data
- Database performance indices (Flyway V9)
- Efficient pagination for large datasets
- Connection pooling via HikariCP
- Docker multi-stage build for minimal image size

## Metrics

| Metric | Value |
| --- | --- |
| Java source files | 60+ |
| REST API endpoints | 12 |
| MVC routes | 18+ |
| Database tables | 10+ |
| Flyway migrations | 10 |
| Integration tests | 5+ |
| Load test scripts | 3 (smoke, load, stress) |
| CI/CD workflows | 2 (release, e2e) |

## Future Improvements

- AI-powered study recommendations based on usage patterns
- Push notifications for booking status changes
- Full mobile app with React Native + AR campus tour
- Self-service data export/deletion API
- Multi-campus federation
