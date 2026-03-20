CREATE INDEX idx_product_specifications ON product USING GIN (specifications);
CREATE INDEX idx_product_active ON product (active);