CREATE TABLE deliveries (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_id         VARCHAR(36)  NOT NULL,
    order_id            VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36)  NOT NULL,
    status              VARCHAR(30)  NOT NULL,
    carrier             VARCHAR(50),
    tracking_number     VARCHAR(100),
    shipping_address    TEXT         NOT NULL,
    estimated_delivery  TIMESTAMP(6) NULL,
    delivered_at        TIMESTAMP(6) NULL,
    failure_reason      VARCHAR(500),
    created_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version             BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT uk_delivery_id UNIQUE (delivery_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;