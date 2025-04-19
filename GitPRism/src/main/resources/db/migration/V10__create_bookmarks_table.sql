CREATE TABLE bookmarks (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           portfolio_id BIGINT NOT NULL,
                           is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                           created_at DATETIME NOT NULL,
                           updated_at DATETIME NOT NULL,

                           CONSTRAINT fk_bookmark_user
                               FOREIGN KEY (user_id)
                                   REFERENCES github_users (id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_bookmark_portfolio
                               FOREIGN KEY (portfolio_id)
                                   REFERENCES portfolios (id)
                                   ON DELETE CASCADE
);
