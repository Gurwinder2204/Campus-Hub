#!/bin/sh
# ============================================================
# Campus Study Hub - Database Backup Script
# ============================================================

# Configuration
BACKUP_DIR="/app/backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
DATABASE_NAME="campus_hub"
BACKUP_FILE="${BACKUP_DIR}/${DATABASE_NAME}_${TIMESTAMP}.sql"

# Create backup directory if it doesn't exist
mkdir -p "${BACKUP_DIR}"

# Run pg_dump
# Note: Assumes PGPASSWORD is set in environment or .pgpass is used
echo "Starting backup for ${DATABASE_NAME}..."
pg_dump -h localhost -U "${DB_USERNAME}" -d "${DATABASE_NAME}" > "${BACKUP_FILE}"

if [ $? -eq 0 ]; then
  echo "Backup successful: ${BACKUP_FILE}"
  # Keep only last 7 days of backups
  find "${BACKUP_DIR}" -type f -name "*.sql" -mtime +7 -delete
  echo "Old backups cleaned up."
else
  echo "Backup failed!"
  exit 1
fi
