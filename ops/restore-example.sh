#!/bin/sh
# ============================================================
# Campus Study Hub — Database Restore Example
# ============================================================
# Restores a PostgreSQL database from a backup file.
#
# Usage:
#   ./ops/restore-example.sh <backup_file.sql.gz>
#
# Example:
#   ./ops/restore-example.sh /app/backups/campus_hub_20260305_030000.sql.gz
#
# WARNING: This will DROP and recreate the database.
# ============================================================

set -e

BACKUP_FILE="$1"

if [ -z "$BACKUP_FILE" ]; then
  echo "Usage: $0 <backup_file.sql.gz>"
  echo "Example: $0 /app/backups/campus_hub_20260305_030000.sql.gz"
  exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
  echo "ERROR: Backup file not found: $BACKUP_FILE"
  exit 1
fi

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-campus_hub}"
DB_USER="${DB_USERNAME:-campus_user}"

echo "=== Campus Study Hub — Database Restore ==="
echo "Source: $BACKUP_FILE"
echo "Target: $DB_NAME @ $DB_HOST:$DB_PORT"
echo ""
echo "⚠️  WARNING: This will DROP and recreate the database."
echo "Press Ctrl+C to abort, or wait 5 seconds to continue..."
sleep 5

# Drop and recreate database
echo "Dropping database..."
PGPASSWORD="$DB_PASSWORD" dropdb -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" --if-exists "$DB_NAME"

echo "Creating database..."
PGPASSWORD="$DB_PASSWORD" createdb -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" "$DB_NAME"

# Restore from backup
echo "Restoring from backup..."
gunzip -c "$BACKUP_FILE" | PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME"

echo ""
echo "✅ Restore complete."
echo "Verify with: psql -h $DB_HOST -U $DB_USER -d $DB_NAME -c 'SELECT count(*) FROM users;'"
