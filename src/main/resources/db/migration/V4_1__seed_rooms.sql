-- ============================================================
-- V4.1: Seed sample rooms
-- ============================================================
INSERT INTO rooms (name, capacity, resources, building, floor, room_number, created_at)
VALUES
    ('Room 101', 30, '{"projector":true,"whiteboard":true,"ac":true}',
     'Main Block', '1', '101', NOW()),
    ('Room 102', 50, '{"projector":true,"whiteboard":true,"ac":true,"smartBoard":true}',
     'Main Block', '1', '102', NOW()),
    ('Library Study Room', 10, '{"whiteboard":true,"powerOutlets":8}',
     'Library', 'Ground', 'LSR-1', NOW())
ON CONFLICT DO NOTHING;
