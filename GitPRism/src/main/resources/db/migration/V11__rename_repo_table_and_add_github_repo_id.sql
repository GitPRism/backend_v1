-- ✅ github_repo_id 컬럼 추가 (조건부)
SET @col := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'repos'
    AND COLUMN_NAME = 'github_repo_id'
    AND TABLE_SCHEMA = DATABASE()
);

SET @sql := IF(@col = 0,
  'ALTER TABLE repos ADD COLUMN github_repo_id BIGINT;',
  'SELECT "github_repo_id already exists";'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ✅ visibility 컬럼 무조건 추가 (조건문 제거)
ALTER TABLE repos ADD COLUMN visibility VARCHAR(50) DEFAULT 'public';

-- ✅ 고유 인덱스 추가 (조건부)
SET @idx := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_NAME = 'repos'
    AND INDEX_NAME = 'idx_github_repo_id_unique'
    AND TABLE_SCHEMA = DATABASE()
);

SET @sql3 := IF(@idx = 0,
  'CREATE UNIQUE INDEX idx_github_repo_id_unique ON repos(github_repo_id);',
  'SELECT "Index already exists";'
);

PREPARE stmt3 FROM @sql3;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;
