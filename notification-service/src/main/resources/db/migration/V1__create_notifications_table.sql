CREATE TABLE notifications (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_id   VARCHAR(36)  NOT NULL,
    user_id           VARCHAR(36)  NOT NULL,
    order_id          VARCHAR(36),
    type              VARCHAR(50)  NOT NULL,
    channel           VARCHAR(20)  NOT NULL,
    recipient         VARCHAR(255) NOT NULL,
    subject           VARCHAR(255),
    content           TEXT         NOT NULL,
    status            VARCHAR(30)  NOT NULL,
    failure_reason    VARCHAR(500),
    sent_at           TIMESTAMP(6) NULL,
    created_at        TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at        TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    version           BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT uk_notification_id UNIQUE (notification_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;