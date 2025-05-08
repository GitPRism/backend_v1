-- GitHubUser 목업 유저 삽입 (id = 1)
INSERT INTO github_users (
    id, github_id, username, email, access_token, created_at, updated_at, deleted
) VALUES (
    1,
    '12345678',
    'mockuser',
    'mockuser@example.com',
    'ghp_mockAccessToken1234567890',
    NOW(),
    NOW(),
    FALSE
);

-- Portfolio 목업 데이터 삽입
INSERT INTO portfolios (id, user_id, title, description, status, is_deleted, created_at, updated_at)
VALUES
    (1, 1, 'Git 포트폴리오 관리 공유 프로젝트', '이 프로젝트는...', 'PUBLISHED', false, '2025-04-10 14:12:00', '2025-04-10 14:12:00'),
    (2, 1, '개발자 일일 회고 자동 기록 서비스', '매일 자동으로 회고...', 'PUBLISHED', false, '2025-04-09 10:30:00', '2025-04-09 10:30:00'),
    (3, 1, 'PR 분석 기반 코드 리뷰 피드백 시스템', 'OpenAI를 활용한...', 'PUBLISHED', false, '2025-04-08 16:45:00', '2025-04-08 16:45:00');