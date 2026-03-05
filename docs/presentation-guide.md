# Presentation Guide — Campus Study Hub

## 5-Minute Presentation Outline

### Slide 1: Title (30 seconds)
**Campus Study Hub — A Comprehensive Campus Services Platform**

> "Good morning/afternoon. Today I'll present Campus Study Hub, a full-stack web application we built for university students to manage study materials, book rooms, and access campus services."

---

### Slide 2: Problem Statement (45 seconds)

**Problems we're solving:**
1. Students struggle to find organized study materials
2. Room booking is manual and error-prone
3. No centralized platform for campus services
4. No performance monitoring or analytics

> "Currently, students share notes on WhatsApp, book rooms by physically going to the office, and there's no way to track what resources are most helpful. We built Campus Study Hub to solve all of these problems."

---

### Slide 3: System Architecture (60 seconds)

Show the high-level architecture diagram from `docs/architecture/system-architecture.md`.

Key points:
- **Spring Boot 3.2** backend with REST APIs and server-rendered UI
- **PostgreSQL** for persistent storage with Flyway migrations
- **Redis** for caching and performance
- **Prometheus + Grafana** for monitoring
- **Docker** for containerized deployment
- **GitHub Actions** for CI/CD

> "Our architecture follows industry best practices. We use Spring Boot for the backend, PostgreSQL for the database, Redis for caching, and Prometheus with Grafana for real-time monitoring."

---

### Slide 4: Key Features — Live Demo (120 seconds)

Walk through:
1. **Login** → Show the dashboard with semester cards
2. **Subjects** → Browse Semester 1 → View notes, papers, videos
3. **Book a Room** → Submit booking → Admin approves
4. **Search** → Search for a subject
5. **Study Planner** → Create a task via API

> "Let me show you the application in action. [Walk through each feature live]"

---

### Slide 5: Security & Performance (30 seconds)

Highlight:
- Rate limiting protects against abuse
- Spring Security with role-based access
- CSP and HSTS security headers
- Load tested with k6 (up to 200 virtual users)

> "Security was a key focus. We implemented rate limiting, security headers, and role-based access control. We've also load tested the system to handle up to 200 concurrent users."

---

### Slide 6: Future Scope (30 seconds)

- Mobile app with AR campus tour
- AI study recommendations
- Push notifications
- Multi-campus federation

> "For future work, we plan to add a mobile app with augmented reality campus navigation, AI-powered study recommendations, and multi-campus support."

---

## Presentation Tips

1. **Practice the demo**: Run `scripts/start-demo.sh` and walk through the flow until smooth
2. **Have a backup**: Take screenshots in case the live demo fails
3. **Time yourself**: The demo portion (Slide 4) is the longest — keep it under 2 minutes
4. **Speak to the audience**: Avoid reading from slides; use them as prompts
5. **Anticipate questions**: Common topics include database choice, security approach, and scalability

## Backup Plan

If live demo fails:
1. Show screenshots from the demo deck
2. Show the `/actuator/health` endpoint (usually always works)
3. Walk through the architecture diagram instead
