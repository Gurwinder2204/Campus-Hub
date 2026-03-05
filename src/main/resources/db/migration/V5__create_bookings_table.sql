-- ============================================================
-- V5: Create bookings table
-- ============================================================
CREATE TABLE IF NOT EXISTS bookings (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL,
    room_id     BIGINT          NOT NULL REFERENCES rooms(id),
    start_at    TIMESTAMP       NOT NULL,
    end_at      TIMESTAMP       NOT NULL,
    status      VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    purpose     TEXT,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_booking_times CHECK (end_at > start_at)
);

CREATE INDEX idx_bookings_room_status ON bookings (room_id, status);
CREATE INDEX idx_bookings_user ON bookings (user_id);
