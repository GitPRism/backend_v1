CREATE TABLE repos (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      git_id VARCHAR(255),
                      repo_name VARCHAR(255),
                      url TEXT,
                      description TEXT,
                      default_branch VARCHAR(255),
                      language VARCHAR(255),
                      created_at TIMESTAMP,
                      updated_at TIMESTAMP,
                      is_deleted BOOLEAN
);
