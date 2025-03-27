CREATE TABLE IF NOT EXISTS git_hub_user (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            github_id VARCHAR(255) NOT NULL,
                                            username VARCHAR(255),
                                            email VARCHAR(255),
                                            access_token VARCHAR(1000),
                                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            deleted BOOLEAN DEFAULT FALSE
);
