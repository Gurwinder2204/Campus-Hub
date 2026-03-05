#!/bin/sh
# ============================================================
# Campus Study Hub — Automated Demo Scenario
# ============================================================
# Simulates a full demo scenario via API calls.
# Requires the backend to be running at BASE_URL.
#
# Usage:
#   ./scripts/demo-scenario.sh [BASE_URL]
# ============================================================

set -e

BASE_URL="${1:-http://localhost:8080}"
ADMIN_EMAIL="${DEMO_ADMIN_EMAIL:-admin@campus.com}"
ADMIN_PASS="${DEMO_ADMIN_PASSWORD:-admin123}"

echo "╔══════════════════════════════════════════════╗"
echo "║     Campus Study Hub — Demo Scenario         ║"
echo "╚══════════════════════════════════════════════╝"
echo ""
echo "Target: $BASE_URL"
echo ""

COOKIE_JAR=$(mktemp)
trap "rm -f $COOKIE_JAR" EXIT

# Step 0: Health check
echo "🔍 Step 0: Health Check"
STATUS=$(curl -sf -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health")
if [ "$STATUS" = "200" ]; then
  echo "  ✅ Backend is healthy"
else
  echo "  ❌ Backend is not responding (HTTP $STATUS)"
  exit 1
fi

# Step 1: Login
echo ""
echo "🔐 Step 1: Login as admin"
LOGIN_STATUS=$(curl -sf -o /dev/null -w "%{http_code}" -c "$COOKIE_JAR" -L \
  -d "username=$ADMIN_EMAIL&password=$ADMIN_PASS" \
  "$BASE_URL/login")
if [ "$LOGIN_STATUS" = "200" ]; then
  echo "  ✅ Logged in as $ADMIN_EMAIL"
else
  echo "  ❌ Login failed (HTTP $LOGIN_STATUS)"
  exit 1
fi

# Step 2: Create a booking
echo ""
echo "📅 Step 2: Create a room booking"
BOOKING_RESP=$(curl -sf -w "\n%{http_code}" -b "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -d '{"roomId":1,"startTime":"2026-04-01T10:00:00","endTime":"2026-04-01T12:00:00","purpose":"Demo Study Session"}' \
  "$BASE_URL/api/v1/bookings" 2>/dev/null || echo "FAILED")
BOOKING_CODE=$(echo "$BOOKING_RESP" | tail -1)
if [ "$BOOKING_CODE" = "201" ] || [ "$BOOKING_CODE" = "200" ]; then
  echo "  ✅ Booking created successfully"
else
  echo "  ⚠  Booking creation returned: $BOOKING_CODE (room may not exist yet)"
fi

# Step 3: List bookings
echo ""
echo "📋 Step 3: List bookings"
LIST_STATUS=$(curl -sf -o /dev/null -w "%{http_code}" -b "$COOKIE_JAR" \
  "$BASE_URL/api/v1/bookings")
if [ "$LIST_STATUS" = "200" ]; then
  echo "  ✅ Bookings listed successfully"
else
  echo "  ⚠  List bookings returned: $LIST_STATUS"
fi

# Step 4: Create a study task
echo ""
echo "📝 Step 4: Create a study task"
TASK_RESP=$(curl -sf -w "\n%{http_code}" -b "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -d '{"title":"Review Demo Notes","description":"Prepare for demo presentation","dueDate":"2026-04-05","priority":"HIGH"}' \
  "$BASE_URL/api/v1/tasks" 2>/dev/null || echo "FAILED")
TASK_CODE=$(echo "$TASK_RESP" | tail -1)
if [ "$TASK_CODE" = "200" ] || [ "$TASK_CODE" = "201" ]; then
  echo "  ✅ Study task created successfully"
else
  echo "  ⚠  Task creation returned: $TASK_CODE"
fi

# Step 5: Track an analytics event
echo ""
echo "📊 Step 5: Track analytics event"
ANALYTICS_STATUS=$(curl -sf -o /dev/null -w "%{http_code}" -b "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -d '{"event_type":"demo_run","payload":{"scenario":"full","timestamp":"2026-03-05"}}' \
  "$BASE_URL/api/v1/analytics/track")
if [ "$ANALYTICS_STATUS" = "202" ]; then
  echo "  ✅ Analytics event tracked"
else
  echo "  ⚠  Analytics returned: $ANALYTICS_STATUS"
fi

# Step 6: Visit dashboard
echo ""
echo "🏠 Step 6: Load dashboard"
DASH_STATUS=$(curl -sf -o /dev/null -w "%{http_code}" -b "$COOKIE_JAR" "$BASE_URL/dashboard")
if [ "$DASH_STATUS" = "200" ]; then
  echo "  ✅ Dashboard loaded successfully"
else
  echo "  ⚠  Dashboard returned: $DASH_STATUS"
fi

echo ""
echo "╔══════════════════════════════════════════════╗"
echo "║       🎉 Demo Scenario Complete!             ║"
echo "╚══════════════════════════════════════════════╝"
