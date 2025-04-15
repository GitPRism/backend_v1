CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        portfolio_id BIGINT NOT NULL,
                                        user_id BIGINT NOT NULL,
                                        comment TEXT NOT NULL,
                                        created_at DATETIME NOT NULL,
                                        updated_at DATETIME NOT NULL,
                                        is_deleted BOOLEAN NOT NULL,

                                        CONSTRAINT fk_comments_portfolio
                                            FOREIGN KEY (portfolio_id) REFERENCES portfolios(id)
                                                ON DELETE CASCADE,

                                        CONSTRAINT fk_comments_user
                                            FOREIGN KEY (user_id) REFERENCES github_users(id)
                                                ON DELETE CASCADE
);
