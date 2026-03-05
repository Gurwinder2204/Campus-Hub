#!/bin/sh
# ============================================================
# Campus Study Hub — One-Click Demo Environment
# ============================================================
# Starts all services, seeds demo data, and prints access URLs.
#
# Usage:
#   ./scripts/start-demo.sh
#
# Prerequisites:
#   - Docker and Docker Compose installed
#   - Ports 8080, 5432, 9090, 3001 available
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "╔══════════════════════════════════════════════╗"
echo "║     Campus Study Hub — Demo Environment      ║"
echo "╚══════════════════════════════════════════════╝"
echo ""

# Step 1: Start docker-compose services
echo "🐳 Starting Docker services..."
cd "$PROJECT_DIR"
docker compose up -d

# Step 2: Wait for database
echo ""
echo "⏳ Waiting for PostgreSQL to be ready..."
for i in $(seq 1 30); do
  if docker compose exec -T db pg_isready -U campus_user 2>/dev/null; then
    echo "  ✅ PostgreSQL is ready"
    break
  fi
  sleep 2
done

# Step 3: Wait for backend
echo ""
echo "⏳ Waiting for backend to start..."
for i in $(seq 1 60); do
  if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "  ✅ Backend is ready"
    break
  fi
  if [ "$i" = "60" ]; then
    echo "  ❌ Backend did not start within 120 seconds"
    exit 1
  fi
  sleep 2
done

# Step 4: Run seed data
echo ""
echo "🌱 Applying seed data..."
if [ -f "$SCRIPT_DIR/seed-data.sh" ]; then
  sh "$SCRIPT_DIR/seed-data.sh"
else
  echo "  ⚠  Seed script not found, skipping..."
fi

# Step 5: Print URLs
echo ""
echo "╔══════════════════════════════════════════════╗"
echo "║          🎉 Demo Environment Ready!          ║"
echo "╠══════════════════════════════════════════════╣"
echo "║                                              ║"
echo "║  Backend:    http://localhost:8080            ║"
echo "║  Health:     http://localhost:8080/actuator   ║"
echo "║  Prometheus: http://localhost:9090            ║"
echo "║  Grafana:    http://localhost:3001            ║"
echo "║                                              ║"
echo "║  Admin Login:                                ║"
echo "║    Email:    admin@campus.com                 ║"
echo "║    Password: admin123                         ║"
echo "║                                              ║"
echo "╚══════════════════════════════════════════════╝"
echo ""
echo "To stop: docker compose down"
