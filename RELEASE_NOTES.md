# Campus Study Hub — v1.0.0 Release Notes

_Released: March 2026_

## 🎉 What's New

### Core Features

- **Study Resources**: Semester-wise notes, question papers, and curated video tutorials
- **Room Booking**: Full booking workflow with admin approval
- **Search**: Full-text search across all subjects
- **User Management**: Role-based access (Admin/Student) with Spring Security

### Campus Services

- Lost & found, complaints, events, and POI management
- Study task planner with deadlines

### Monitoring & Performance

- Prometheus metrics + Grafana dashboards (`/actuator/prometheus`)
- Redis caching for POI endpoints
- Database performance indices (Flyway V9)

### Security & Multi-tenancy

- Rate limiting via Bucket4j (10 req/min on auth, 100 on API)
- Security headers: CSP, HSTS, Referrer-Policy, Permissions-Policy
- Optional multi-tenant isolation via `X-Tenant-ID` header
- Graceful shutdown with 30s timeout

### Infrastructure

- Docker multi-stage build (JDK 17)
- Production profile (`application-prod.properties`)
- Automated database backups (`ops/auto-backup.sh`)
- k6 load testing suite (smoke, load, stress)

### CI/CD

- GitHub Actions release workflow (tag-triggered + manual)
- Cypress E2E test suite with CI job
- Release candidate pipeline

## 📚 Documentation

- [Release Checklist](docs/release-checklist.md)
- [Demo Materials](docs/demo/)
- [Privacy Policy](docs/privacy-policy.md)
- [Accessibility Report](docs/accessibility-report.md)
- [Handoff Guide](docs/handoff.md)

## ⚠️ Known Limitations

- Firebase Admin SDK imports show IDE warnings (compiles fine)
- Some `text-muted` color contrast on dark theme is below AA for small text
- Data export API (`DELETE /api/v1/users/{id}/data-export`) not yet automated
- Mobile AR features require separate React Native setup

## 🔮 Next Steps

- Self-service data export/deletion endpoint
- AI-powered study recommendations
- Push notifications via FCM (infrastructure ready)
- Full mobile app deployment (Android APK + iOS)
