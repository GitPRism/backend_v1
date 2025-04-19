DROP TABLE IF EXISTS pull_requests;

CREATE TABLE pull_requests (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,       -- 내부용 PK
                               github_pr_id BIGINT,                        -- GitHub PR ID
                               number INT,
                               pr_title VARCHAR(255),
                               pr_details TEXT,
                               draft BOOLEAN,
                               created_at DATETIME,
                               updated_at DATETIME,
                               merged_at DATETIME,
                               author VARCHAR(255),
                               sha VARCHAR(255),
                               source_branch VARCHAR(255),
                               target_branch VARCHAR(255),
                               html_url TEXT,
                               labels TEXT,
                               repo_id BIGINT,
                               is_deleted BOOLEAN DEFAULT FALSE
);
