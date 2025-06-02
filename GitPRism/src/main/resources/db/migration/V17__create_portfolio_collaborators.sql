CREATE TABLE portfolio_collaborators (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         portfolio_id BIGINT NOT NULL,
                                         user_id BIGINT NOT NULL,
                                         role VARCHAR(20) NOT NULL,
                                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT fk_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolios(id),
                                         CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES github_users(id)
);
