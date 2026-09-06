CREATE TABLE payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id      VARCHAR(36)  NOT NULL,
    order_id        VARCHAR(36)  NOT NULL,
    user_id         VARCHAR(36)  NOT NULL,
    amount          DECIMAL(19,4) NOT NULL,
    currency        VARCHAR(3)   NOT NULL,
    status          VARCHAR(30)  NOT NULL,
    payment_method  VARCHAR(50),
    transaction_ref VARCHAR(100),
    failure_reason  VARCHAR(500),
    created_at      TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version         BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT uk_payment_id UNIQUE (payment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;