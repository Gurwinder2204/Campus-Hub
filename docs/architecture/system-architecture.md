# System Architecture — Campus Study Hub

## 1. High-Level Architecture

```mermaid
graph TB
    subgraph Client["Client Layer"]
        Browser["Web Browser"]
        Mobile["Mobile App (React Native)"]
    end

    subgraph Frontend["Frontend"]
        Thymeleaf["Thymeleaf Templates + Bootstrap 5"]
    end

    subgraph Backend["Backend (Spring Boot 3.2)"]
        Controllers["REST + MVC Controllers"]
        Services["Service Layer"]
        Security["Spring Security + Rate Limiting"]
        Tenant["Multi-Tenant Filter"]
    end

    subgraph Data["Data Layer"]
        PostgreSQL["PostgreSQL"]
        Redis["Redis Cache"]
        FileStore["File Storage (uploads/)"]
    end

    subgraph Monitoring["Monitoring"]
        Prometheus["Prometheus"]
        Grafana["Grafana"]
        Actuator["Spring Actuator"]
    end

    subgraph External["External Services"]
        Firebase["Firebase Cloud Messaging"]
    end

    Browser --> Thymeleaf
    Mobile --> Controllers
    Thymeleaf --> Controllers
    Controllers --> Security
    Security --> Tenant
    Tenant --> Services
    Services --> PostgreSQL
    Services --> Redis
    Services --> FileStore
    Services --> Firebase
    Actuator --> Prometheus
    Prometheus --> Grafana
```

## 2. Backend Component Architecture

```mermaid
graph LR
    subgraph Controllers
        AuthC["AuthController"]
        AdminC["AdminController"]
        StudentC["StudentController"]
        BookingC["BookingController"]
        TaskC["StudyTaskController"]
        AnalyticsC["AnalyticsController"]
        NotifC["NotificationController"]
        FileC["FileController"]
    end

    subgraph Services
        UserSvc["UserService"]
        SubjectSvc["SubjectService"]
        ResourceSvc["ResourceService"]
        BookingSvc["BookingService"]
        TaskSvc["StudyTaskService"]
        AnalyticsSvc["AnalyticsService"]
        NotifSvc["NotificationService"]
    end

    subgraph Repositories
        UserRepo["UserRepository"]
        SubjectRepo["SubjectRepository"]
        BookingRepo["BookingRepository"]
        RoomRepo["RoomRepository"]
        TaskRepo["StudyTaskRepository"]
        EventRepo["AnalyticsEventRepository"]
    end

    AuthC --> UserSvc
    AdminC --> SubjectSvc
    AdminC --> ResourceSvc
    AdminC --> BookingSvc
    StudentC --> SubjectSvc
    BookingC --> BookingSvc
    TaskC --> TaskSvc
    AnalyticsC --> AnalyticsSvc
    NotifC --> UserRepo
    FileC --> ResourceSvc

    UserSvc --> UserRepo
    SubjectSvc --> SubjectRepo
    BookingSvc --> BookingRepo
    BookingSvc --> RoomRepo
    TaskSvc --> TaskRepo
    AnalyticsSvc --> EventRepo
```

## 3. Data Flow Diagram

```mermaid
sequenceDiagram
    actor Student
    participant Browser
    participant Backend as Spring Boot
    participant DB as PostgreSQL
    participant Cache as Redis
    participant FCM as Firebase

    Student->>Browser: Login
    Browser->>Backend: POST /login
    Backend->>DB: Validate credentials
    DB-->>Backend: User record
    Backend-->>Browser: Session + redirect to dashboard

    Student->>Browser: View subjects
    Browser->>Backend: GET /semesters
    Backend->>DB: Query subjects
    DB-->>Backend: Subject list
    Backend-->>Browser: Render Thymeleaf template

    Student->>Browser: Book a room
    Browser->>Backend: POST /api/v1/bookings
    Backend->>DB: Create booking (PENDING)
    Backend->>FCM: Notify admin
    DB-->>Backend: Booking record
    Backend-->>Browser: 201 Created

    Student->>Browser: Download note
    Browser->>Backend: GET /files/notes/{id}/download
    Backend->>DB: Lookup file metadata
    Backend-->>Browser: File stream
```

## 4. AR Navigation Integration Flow

```mermaid
sequenceDiagram
    actor Student
    participant App as Mobile App
    participant Camera as Device Camera
    participant Backend as Spring Boot API
    participant DB as PostgreSQL

    Student->>App: Open AR Tour
    App->>Backend: GET /api/v1/pois
    Backend->>DB: Query Points of Interest
    DB-->>Backend: POI list with coordinates
    Backend-->>App: JSON POI data

    Student->>App: Point camera at building
    App->>Camera: Activate AR overlay
    Camera-->>App: Camera feed
    App->>App: Match GPS + compass to POI data
    App-->>Student: Display AR markers on screen

    Student->>App: Tap AR marker
    App->>Backend: GET /api/v1/pois/{id}
    Backend->>DB: POI details
    DB-->>Backend: POI record
    Backend-->>App: POI details + images
    App-->>Student: Show POI info card
```

## Technology Stack Summary

| Component | Technology | Purpose |
| --- | --- | --- |
| Backend | Spring Boot 3.2, Java 17 | REST API + MVC |
| Frontend | Thymeleaf, Bootstrap 5 | Server-side rendered UI |
| Database | PostgreSQL 15 | Persistent storage |
| Cache | Redis | POI + QR code caching |
| Auth | Spring Security | Session-based auth |
| Monitoring | Prometheus + Grafana | Metrics + dashboards |
| Notifications | Firebase Admin SDK | Push notifications |
| Migrations | Flyway | Database schema versioning |
| CI/CD | GitHub Actions | Build, test, release |
| Container | Docker | Production deployment |
