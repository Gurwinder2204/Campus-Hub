#!/bin/sh
# ============================================================
# Campus Study Hub — Demo Seed Data Script
# ============================================================
# This script applies seed data for demo/dev environments.
# It is idempotent — safe to run multiple times.
#
# Usage:
#   ./scripts/seed-data.sh
#
# Environment Variables:
#   DATABASE_URL  — JDBC URL (default: jdbc:postgresql://localhost:5432/campus_hub)
#   DB_USERNAME   — Database user (default: campus_user)
#   DB_PASSWORD   — Database password (default: campus_password)
# ============================================================

set -e

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-campus_hub}"
DB_USER="${DB_USERNAME:-campus_user}"

SEED_DIR="$(cd "$(dirname "$0")/../src/main/resources/db/seed" && pwd)"

echo "=== Campus Study Hub — Seed Data ==="
echo "Host: $DB_HOST:$DB_PORT"
echo "Database: $DB_NAME"
echo "Seed directory: $SEED_DIR"
echo ""

# Apply each SQL file in the seed directory
for sql_file in "$SEED_DIR"/*.sql; do
  if [ -f "$sql_file" ]; then
    echo "Applying: $(basename "$sql_file")"
    PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$sql_file"
    echo "  ✅ Done"
  fi
done

echo ""
echo "=== Seed data applied successfully ==="
echo ""
echo "To run in Docker Compose dev environment:"
echo "  docker compose exec db psql -U campus_user -d campus_hub -f /seed/demo-seed.sql"
