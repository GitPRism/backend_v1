ALTER TABLE portfolios
    ADD COLUMN parent_id BIGINT AFTER repo_id;

ALTER TABLE portfolios
    ADD CONSTRAINT fk_portfolio_parent
        FOREIGN KEY (parent_id)
            REFERENCES portfolios(id)
            ON DELETE SET NULL;
