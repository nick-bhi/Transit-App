-- Sample Transit Data for Testing

-- Insert sample stops
INSERT INTO stops (id, name, lat, lon) VALUES 
('STOP_001', 'Main Street Station', 40.7589, -73.9851),
('STOP_002', 'Central Plaza', 40.7614, -73.9776),
('STOP_003', 'Park Avenue', 40.7489, -73.9680);

-- Insert sample routes
INSERT INTO routes (id, short_name, long_name) VALUES 
('M15', 'M15', 'First/Second Avenue'),
('M34', 'M34', 'Crosstown'),
('BX19', 'BX19', 'Willis Avenue');

-- Insert sample scheduled stop times (for reference)
INSERT INTO stop_times (trip_id, route_id, stop_id, stop_sequence, scheduled_arrival_ts) VALUES
('TRIP_001', 'M15', 'STOP_001', 1, EXTRACT(EPOCH FROM NOW() + INTERVAL '10 minutes') * 1000),
('TRIP_001', 'M15', 'STOP_002', 2, EXTRACT(EPOCH FROM NOW() + INTERVAL '15 minutes') * 1000),
('TRIP_002', 'M34', 'STOP_002', 1, EXTRACT(EPOCH FROM NOW() + INTERVAL '5 minutes') * 1000);

-- Verify
SELECT 'Stops:', COUNT(*) FROM stops;
SELECT 'Routes:', COUNT(*) FROM routes;
SELECT 'Stop Times:', COUNT(*) FROM stop_times;

