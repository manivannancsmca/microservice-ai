CREATE TABLE products (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    sku             VARCHAR(50)     NOT NULL,
    name            VARCHAR(200)    NOT NULL,
    description     TEXT            NULL,
    price           DECIMAL(12,2)   NOT NULL,
    category        VARCHAR(100)    NOT NULL,
    brand           VARCHAR(100)    NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version         BIGINT          NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    CONSTRAINT uk_products_sku UNIQUE (sku),
    CONSTRAINT chk_products_price CHECK (price >= 0),
    CONSTRAINT chk_products_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;