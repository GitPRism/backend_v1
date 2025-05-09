-- 포트폴리오 테이블에 레포 외래 키 컬럼 추가
ALTER TABLE portfolios
    ADD COLUMN repo_id BIGINT AFTER user_id;

-- repo_id → repos 테이블의 id 를 참조하는 외래키 제약조건 설정
ALTER TABLE portfolios
    ADD CONSTRAINT fk_portfolio_repo
        FOREIGN KEY (repo_id)
            REFERENCES repos(id);
