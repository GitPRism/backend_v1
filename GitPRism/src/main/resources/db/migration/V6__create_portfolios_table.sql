CREATE TABLE IF NOT EXISTS portfolios (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          title VARCHAR(255) NOT NULL,
                                          description TEXT,
                                          status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'PUBLISHED')),
                                          user_id BIGINT NOT NULL,
                                          created_at DATETIME NOT NULL,
                                          updated_at DATETIME NOT NULL,
                                          is_deleted BOOLEAN NOT NULL,

                                          CONSTRAINT fk_portfolio_user
                                              FOREIGN KEY (user_id) REFERENCES github_users(id)
                                                  ON DELETE CASCADE
);
