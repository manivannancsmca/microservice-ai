CREATE INDEX idx_deliveries_order_id     ON deliveries (order_id);
CREATE INDEX idx_deliveries_user_id      ON deliveries (user_id);
CREATE INDEX idx_deliveries_status       ON deliveries (status);
CREATE INDEX idx_deliveries_tracking     ON deliveries (tracking_number);
CREATE INDEX idx_deliveries_created_at   ON deliveries (created_at);