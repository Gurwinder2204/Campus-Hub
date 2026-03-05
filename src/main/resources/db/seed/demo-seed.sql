-- ============================================================
-- Campus Study Hub — Demo Seed Data
-- ============================================================
-- This script is idempotent: it checks for existence before inserting.
-- Run ONLY in dev/demo environments.
-- ============================================================

-- Sample Rooms (3)
INSERT INTO rooms (name, building, capacity, has_projector, has_whiteboard, is_available, tenant_id)
SELECT 'Quiet Study Room A', 'Library Building', 10, true, true, true, 'default'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE name = 'Quiet Study Room A');

INSERT INTO rooms (name, building, capacity, has_projector, has_whiteboard, is_available, tenant_id)
SELECT 'Group Study Room B', 'Engineering Block', 20, true, false, true, 'default'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE name = 'Group Study Room B');

INSERT INTO rooms (name, building, capacity, has_projector, has_whiteboard, is_available, tenant_id)
SELECT 'Seminar Hall C', 'Main Building', 50, true, true, true, 'default'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE name = 'Seminar Hall C');

-- Sample Bookings (PENDING state, references first room and admin user)
-- Note: These require rooms and users to exist already.
-- The DataLoader creates the admin user on startup.
-- Booking inserts are handled by the seed-data.sh script after verifying IDs.
