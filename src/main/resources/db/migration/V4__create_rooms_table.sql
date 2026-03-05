-- ============================================================
-- V4: Create rooms table
-- ============================================================
CREATE TABLE IF NOT EXISTS rooms (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(255)    NOT NULL,
    capacity        INT             NOT NULL CHECK (capacity > 0),
    resources       TEXT,
    building        VARCHAR(255)    NOT NULL,
    floor           VARCHAR(50),
    room_number     VARCHAR(50),
    availability_json TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);
