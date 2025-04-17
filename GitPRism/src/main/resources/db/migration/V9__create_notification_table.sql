CREATE TABLE notifications (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,

                               user_id BIGINT NOT NULL,              -- 알림 받는 사람
                               sender_id BIGINT NOT NULL,            -- 알림 보낸 사람
                               portfolio_id BIGINT NOT NULL,         -- 알림 대상 포트폴리오

                               type ENUM('COMMENT_CREATED', 'LIKE_ADDED', 'BOOKMARK_ADDED') NOT NULL COMMENT '알림 타입',

                               message TEXT,                         -- 알림 캐시 메시지 (선택)
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               redirect_url VARCHAR(255),            -- 클릭 시 이동 경로 (ex: /portfolios/1)

                               created_at DATETIME NOT NULL,
                               updated_at DATETIME NOT NULL,
                               is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

                               CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES github_users (id),
                               CONSTRAINT fk_notifications_sender FOREIGN KEY (sender_id) REFERENCES github_users (id),
                               CONSTRAINT fk_notifications_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolios (id)
);
