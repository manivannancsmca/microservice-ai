CREATE INDEX idx_payments_order_id ON payments (order_id);
CREATE INDEX idx_payments_user_id  ON payments (user_id);
CREATE INDEX idx_payments_status   ON payments (status);
CREATE INDEX idx_payments_created  ON payments (created_at);