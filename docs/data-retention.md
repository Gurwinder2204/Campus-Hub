# Data Retention Policy — Campus Study Hub

## Retention Schedule

| Data Type | Retention Period | Cleanup Method |
| --- | --- | --- |
| Application logs | 30 days | Log rotation (logback) |
| Analytics events | 90 days | Scheduled SQL cleanup |
| Booking records | 1 year | Manual archive |
| Uploaded files | Until deleted | User/admin action |
| User accounts | Until deletion request | Admin action |
| Database backups | 14 days | `ops/auto-backup.sh` prune |

## Automated Cleanup Examples

### Delete analytics events older than 90 days

```sql
DELETE FROM analytics_events WHERE created_at < NOW() - INTERVAL '90 days';
```

### Cron job (run daily at 2 AM)

```bash
# Add to crontab:
0 2 * * * psql -U campus_user -d campus_hub -c "DELETE FROM analytics_events WHERE created_at < NOW() - INTERVAL '90 days';"
```

### Delete unsigned guest uploads older than 30 days

```bash
# Find and remove orphaned files
find /app/uploads -type f -mtime +30 -name "guest_*" -delete
```

## User Data Export / Deletion

> **Note**: Automated data export (`DELETE /api/v1/users/{id}/data-export`) is not yet implemented. For now, this is a manual procedure:

1. Admin runs a query to export user data (bookings, uploads, analytics).
2. Admin deletes user records from all related tables.
3. Admin removes uploaded files from the filesystem.
4. Confirmation sent to user within 14 business days.

A future release will add a self-service data export endpoint.
