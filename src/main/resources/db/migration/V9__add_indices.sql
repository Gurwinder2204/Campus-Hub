-- ============================================================
-- V9: Add indices for performance optimization
-- ============================================================

-- Index for searching rooms by building and floor
CREATE INDEX idx_rooms_building_floor ON rooms(building, floor);

-- Index for subject lookups by semester (often used in dashboard/search)
CREATE INDEX idx_subjects_semester_id ON subjects(semester_id);

-- Composite index for analytics queries filtered by type and time
CREATE INDEX idx_analytics_type_created ON analytics_events(event_type, created_at);
