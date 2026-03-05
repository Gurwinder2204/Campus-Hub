# Campus Study Hub — Demo Script (3–5 minutes)

## Before You Start

- Ensure the backend is running (`./mvnw spring-boot:run`)
- Seed data has been loaded (see `scripts/seed-data.sh`)
- Browser open at `http://localhost:8080`

---

## 0:00 – 0:30 — Introduction

> "Welcome to Campus Study Hub — a one-stop platform for students to access study materials, book rooms, and use campus services. Let me walk you through the key features."

---

## 0:30 – 1:00 — Login & Dashboard

1. Navigate to `/login`
2. Enter admin credentials: `admin@campus.com` / `admin123`
3. Click **Login**
4. **Expected**: Redirected to Dashboard with semester cards

> "Once logged in, students see their personalized dashboard with quick access to all 8 semesters."

---

## 1:00 – 2:00 — Study Resources

1. Click **Semester 1**
2. Click any subject (e.g., "Data Structures")
3. Show the Notes section — click **Download** on a note
4. Show the Question Papers section
5. Show the Video Tutorials section — click **Watch** on a video

> "Each subject has organized notes, question papers, and curated video tutorials. Students can download PDFs directly."

---

## 2:00 – 2:45 — Room Booking

1. Click **Book Room** in the navigation
2. Select a room, pick start/end time, enter purpose
3. Click **Submit Booking Request**
4. Navigate to **Admin Panel** → **Manage Bookings**
5. **Approve** the booking

> "Room booking has an approval workflow. Students submit requests, and admins can approve or reject them."

---

## 2:45 – 3:30 — Search & Monitoring

1. Click **Search** → type "Operating" → show results
2. Open a new tab → navigate to `/actuator/health`
3. Show the health check response: `{"status": "UP"}`
4. Mention Prometheus metrics at `/actuator/prometheus`

> "We have full-text search across subjects, and the application exposes health and performance metrics via Spring Actuator and Prometheus."

---

## 3:30 – 4:00 — Security Features

> "Under the hood, we've implemented rate limiting using Bucket4j, security headers including CSP and HSTS, graceful shutdown, and an optional multi-tenant architecture for per-campus isolation."

---

## 4:00 – 4:30 — Wrap-up

> "Campus Study Hub is production-ready with Docker support, CI/CD pipelines, database backups, and load testing. Thank you for watching!"
