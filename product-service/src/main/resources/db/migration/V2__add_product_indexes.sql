CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_products_status ON products (status);
CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_category_status ON products (category, status);