CREATE INDEX idx_notifications_user_id     ON notifications (user_id);
CREATE INDEX idx_notifications_order_id    ON notifications (order_id);
CREATE INDEX idx_notifications_status      ON notifications (status);
CREATE INDEX idx_notifications_channel     ON notifications (channel);
CREATE INDEX idx_notifications_created_at  ON notifications (created_at);