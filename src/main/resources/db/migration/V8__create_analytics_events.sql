-- V8: Create analytics_events table
CREATE TABLE analytics_events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB,
    user_id VARCHAR(255),
    tenant_id VARCHAR(100),
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_analytics_event_type ON analytics_events(event_type);
CREATE INDEX idx_analytics_created_at ON analytics_events(created_at);
