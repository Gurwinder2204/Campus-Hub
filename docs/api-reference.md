# API Reference — Campus Study Hub

Base URL: `http://localhost:8080`

---

## Authentication

### Login

```
POST /login
Content-Type: application/x-www-form-urlencoded
```

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `username` | string | ✅ | User email |
| `password` | string | ✅ | User password |

**Success**: Redirect to `/dashboard` with session cookie

**Error**: Redirect to `/login?error`

---

### Register

```
POST /register
Content-Type: application/x-www-form-urlencoded
```

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `name` | string | ✅ | Full name |
| `email` | string | ✅ | Email address |
| `password` | string | ✅ | Password (min 6 chars) |

---

### Logout

```
POST /logout
```

Invalidates the session and redirects to `/login?logout`.

---

## Booking APIs

Base path: `/api/v1/bookings`

### Create Booking

```
POST /api/v1/bookings
Content-Type: application/json
Authorization: Session cookie
```

**Request Body:**
```json
{
  "roomId": 1,
  "startTime": "2026-03-10T10:00:00",
  "endTime": "2026-03-10T12:00:00",
  "purpose": "Study group meeting"
}
```

**Response (201):**
```json
{
  "id": 1,
  "roomName": "Quiet Study Room A",
  "startTime": "2026-03-10T10:00:00",
  "endTime": "2026-03-10T12:00:00",
  "status": "PENDING",
  "purpose": "Study group meeting"
}
```

**Error (400):**
```json
{
  "error": "Room is already booked for this time slot"
}
```

### List Bookings

```
GET /api/v1/bookings
Authorization: Session cookie
```

Admin sees all bookings; students see only their own.

**Response (200):**
```json
[
  {
    "id": 1,
    "roomName": "Quiet Study Room A",
    "startTime": "2026-03-10T10:00:00",
    "endTime": "2026-03-10T12:00:00",
    "status": "PENDING"
  }
]
```

### Get Booking

```
GET /api/v1/bookings/{id}
```

### Approve Booking (Admin)

```
PUT /api/v1/bookings/{id}/approve
```

**Response (200):**
```json
{
  "id": 1,
  "status": "APPROVED"
}
```

### Reject Booking (Admin)

```
PUT /api/v1/bookings/{id}/reject
Content-Type: application/json
```

**Request Body:**
```json
{
  "reason": "Room under maintenance"
}
```

### Cancel Booking

```
PUT /api/v1/bookings/{id}/cancel
```

---

## Study Planner APIs

Base path: `/api/v1/tasks`

### List Tasks

```
GET /api/v1/tasks
Authorization: Session cookie
```

**Response (200):**
```json
[
  {
    "id": 1,
    "title": "Complete DSA Assignment",
    "description": "Solve chapter 5 problems",
    "dueDate": "2026-03-15",
    "status": "TODO",
    "priority": "HIGH"
  }
]
```

### Create Task

```
POST /api/v1/tasks
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Complete DSA Assignment",
  "description": "Solve chapter 5 problems",
  "dueDate": "2026-03-15",
  "priority": "HIGH"
}
```

### Update Task Status

```
PATCH /api/v1/tasks/{id}/status?status=IN_PROGRESS
```

Valid statuses: `TODO`, `IN_PROGRESS`, `DONE`

### Delete Task

```
DELETE /api/v1/tasks/{id}
```

**Response**: `204 No Content`

---

## Analytics APIs

Base path: `/api/v1/analytics`

### Track Event

```
POST /api/v1/analytics/track
Content-Type: application/json
```

**Request Body:**
```json
{
  "event_type": "page_view",
  "payload": {
    "page": "/subjects/1",
    "duration_ms": 5000
  }
}
```

**Response**: `202 Accepted`

**Error (400)**: Empty or missing `event_type`

---

## Notification APIs

Base path: `/api/v1/notifications`

### Register Device Token

```
POST /api/v1/notifications/tokens?token=fcm_device_token_here
```

**Response**: `200 OK`

### Unregister Device Token

```
DELETE /api/v1/notifications/tokens?token=fcm_device_token_here
```

**Response**: `204 No Content`

---

## Admin MVC Routes

These are server-rendered pages (not REST APIs).

| Method | Path | Description |
| --- | --- | --- |
| GET | `/admin/dashboard` | Admin dashboard |
| GET | `/admin/subjects` | List subjects |
| POST | `/admin/subjects` | Create subject |
| GET | `/admin/subjects/{id}/edit` | Edit subject form |
| POST | `/admin/subjects/{id}` | Update subject |
| POST | `/admin/subjects/{id}/delete` | Delete subject |
| GET | `/admin/upload/note` | Note upload form |
| POST | `/admin/upload/note` | Upload note |
| POST | `/admin/notes/{id}/delete` | Delete note |
| GET | `/admin/upload/paper` | Paper upload form |
| POST | `/admin/upload/paper` | Upload paper |
| POST | `/admin/papers/{id}/delete` | Delete paper |
| GET | `/admin/add/video` | Video link form |
| POST | `/admin/add/video` | Add video link |
| POST | `/admin/videos/{id}/delete` | Delete video |
| GET | `/admin/bookings` | Pending bookings |
| POST | `/admin/bookings/{id}/approve` | Approve booking |
| POST | `/admin/bookings/{id}/reject` | Reject booking |

---

## Health & Monitoring

| Endpoint | Description |
| --- | --- |
| `GET /actuator/health` | Application health check |
| `GET /actuator/prometheus` | Prometheus metrics |
| `GET /actuator/info` | Application info |
