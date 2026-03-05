# Handoff Guide — Campus Study Hub

## Architecture Overview

```
campus_study/
├── src/main/java/com/campusstudyhub/
│   ├── config/          # Security, Firebase, Spring config
│   ├── controller/      # MVC controllers (web + REST)
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # Tenant, rate limiting filters
│   └── service/         # Business logic
├── src/main/resources/
│   ├── db/migration/    # Flyway migrations (V1-V10)
│   ├── db/seed/         # Demo seed data
│   └── templates/       # Thymeleaf HTML templates
├── scripts/             # Build, seed, and load test scripts
├── ops/                 # Backup and restore scripts
├── docs/                # All documentation
├── .github/workflows/   # CI/CD pipelines
└── Dockerfile           # Production Docker image
```

## Quick Start (Local Development)

```bash
# 1. Clone the repo
git clone https://github.com/Gurwinder2204/Campus-Hub.git
cd Campus-Hub

# 2. Start PostgreSQL (using Docker)
docker compose up db -d

# 3. Run the application
./mvnw spring-boot:run

# 4. Open browser
# http://localhost:8080
# Default admin: admin@campus.com / admin123
```

## Deploy on Render

1. Create a **PostgreSQL** database on Render
2. Create a **Web Service** pointing to this repo
3. Set environment variables:
   - `DATABASE_URL` — Render PostgreSQL connection string
   - `DB_USERNAME` / `DB_PASSWORD` — Database credentials
   - `APP_ADMIN_EMAIL` / `APP_ADMIN_PASSWORD` — Admin credentials
4. Build command: `./mvnw clean package -DskipTests`
5. Start command: `java -jar target/*.jar`

## Onboarding New Contributors

### Branching Model

- `main` — production-ready code
- `feature/*` — new features
- `fix/*` — bug fixes
- `docs/*` — documentation changes

### Running Tests

```bash
./mvnw test                    # Unit + integration tests
npx cypress run                # E2E tests (requires running backend)
k6 run scripts/load-tests/smoke-test.js  # Load tests
```

### Code Style

- Java 17 with Spring Boot 3.2.x conventions
- 4-space indentation
- Commit messages: `type(scope): description` (see CONTRIBUTING.md)

## Contact & Escalation

| Role | Contact |
| --- | --- |
| Project Lead | [Your Name] |
| Backend | [Backend Dev] |
| Frontend | [Frontend Dev] |
| DevOps | [DevOps Engineer] |
