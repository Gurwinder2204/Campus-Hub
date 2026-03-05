# Analytics and Event Tracking

This project uses a custom analytics system to track key user actions and system events. Data is stored in the `analytics_events` table and can be tracked both from the backend and frontend.

## Tracked Events

| Event Type | Description | Payload Example |
|------------|-------------|-----------------|
| `booking_create` | A new room booking request is created. | `{"bookingId": 12, "roomId": 1, "roomName": "Room 101"}` |
| `booking_approve`| A booking is approved by an admin. | `{"bookingId": 12, "roomId": 1}` |
| `booking_reject` | A booking is rejected by an admin. | `{"bookingId": 12, "reason": "Conflict"}` |
| `resource_upload`| A new resource (note, paper, video) is added. | `{"type": "note", "id": 5, "title": "Math Notes"}` |
| `study_task_create`| A new study task is created by a student. | `{"taskId": 101, "title": "Finish Assignment"}` |
| `study_task_status_change`| A study task status is updated. | `{"taskId": 101, "newStatus": "COMPLETED"}` |

## API Endpoint

**POST** `/api/v1/analytics/track`

Tracks a generic event.

**Payload:**

```json
{
  "event_type": "string",
  "payload": {
    "key": "value"
  }
}
```

## Implementation Details

- **Non-blocking**: The `AnalyticsService` uses Spring's `@Async` annotation to ensure that event tracking does not block the main request execution thread.
- **Persistence**: Events are stored in PostgreSQL using the `jsonb` column type for flexible meta-data.
- **Retention**: It is recommended to implement a partition-based retention policy or a cleanup script to prune events older than 90 days in production.
