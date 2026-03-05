#!/bin/sh
# ============================================================
# Campus Study Hub — Automated Database Backup
# ============================================================
# Backs up PostgreSQL database and optionally uploads to S3/MinIO.
# Prunes backups older than BACKUP_RETENTION_DAYS.
#
# Usage:
#   ./ops/auto-backup.sh
#
# Environment Variables:
#   DB_HOST               (default: localhost)
#   DB_PORT               (default: 5432)
#   DB_NAME               (default: campus_hub)
#   DB_USERNAME           (default: campus_user)
#   DB_PASSWORD           (required for pg_dump)
#   BACKUP_DIR            (default: /app/backups)
#   BACKUP_RETENTION_DAYS (default: 14)
#   BACKUP_S3_PREFIX      (optional, e.g. s3://bucket/prefix)
#
# Scheduling (crontab example — daily at 3 AM):
#   0 3 * * * /path/to/ops/auto-backup.sh >> /var/log/campus-backup.log 2>&1
#
# GitHub Actions scheduled workflow (example):
#   on:
#     schedule:
#       - cron: '0 3 * * *'
# ============================================================

set -e

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-campus_hub}"
DB_USER="${DB_USERNAME:-campus_user}"
BACKUP_DIR="${BACKUP_DIR:-/app/backups}"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-14}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/${DB_NAME}_${TIMESTAMP}.sql.gz"

echo "[$(date)] Starting backup for ${DB_NAME}..."

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Run pg_dump and compress
PGPASSWORD="$DB_PASSWORD" pg_dump \
  -h "$DB_HOST" \
  -p "$DB_PORT" \
  -U "$DB_USER" \
  -d "$DB_NAME" \
  --format=plain \
  | gzip > "$BACKUP_FILE"

echo "[$(date)] Backup created: $BACKUP_FILE ($(du -h "$BACKUP_FILE" | cut -f1))"

# Upload to S3/MinIO if configured
if [ -n "$BACKUP_S3_PREFIX" ]; then
  S3_KEY="${BACKUP_S3_PREFIX}/${DB_NAME}_${TIMESTAMP}.sql.gz"
  echo "[$(date)] Uploading to $S3_KEY..."
  aws s3 cp "$BACKUP_FILE" "$S3_KEY"
  echo "[$(date)] Upload complete."
fi

# Prune old local backups
echo "[$(date)] Pruning backups older than ${RETENTION_DAYS} days..."
find "$BACKUP_DIR" -type f -name "*.sql.gz" -mtime +${RETENTION_DAYS} -delete
echo "[$(date)] Prune complete."

echo "[$(date)] Backup finished successfully."
