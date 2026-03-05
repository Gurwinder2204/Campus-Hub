-- ============================================================
-- V10: Add tenant_id to core tables for multi-tenancy
-- ============================================================

-- Add tenant_id to users
ALTER TABLE users ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
CREATE INDEX idx_users_tenant_id ON users(tenant_id);

-- Add tenant_id to rooms
ALTER TABLE rooms ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
CREATE INDEX idx_rooms_tenant_id ON rooms(tenant_id);

-- Add tenant_id to bookings
ALTER TABLE bookings ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
CREATE INDEX idx_bookings_tenant_id ON bookings(tenant_id);

-- Add tenant_id to study_tasks
ALTER TABLE study_tasks ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
CREATE INDEX idx_study_tasks_tenant_id ON study_tasks(tenant_id);

-- Add tenant_id to notes
ALTER TABLE notes ADD COLUMN tenant_id VARCHAR(50) NOT NULL DEFAULT 'default';
CREATE INDEX idx_notes_tenant_id ON notes(tenant_id);
