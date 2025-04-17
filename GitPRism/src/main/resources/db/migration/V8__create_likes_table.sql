CREATE TABLE likes (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       portfolio_id BIGINT NOT NULL,
                       user_id BIGINT NOT NULL,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

                       CONSTRAINT fk_likes_portfolio
                           FOREIGN KEY (portfolio_id) REFERENCES portfolios(id)
                               ON DELETE CASCADE,

                       CONSTRAINT fk_likes_user
                           FOREIGN KEY (user_id) REFERENCES github_users(id)
                               ON DELETE CASCADE
);

-- 인덱스 추가 (성능 향상)
CREATE INDEX idx_likes_user_id ON likes(user_id);
CREATE INDEX idx_likes_portfolio_id ON likes(portfolio_id);
