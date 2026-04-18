ALTER TABLE users
    ADD COLUMN account_id BIGINT UNIQUE,
ADD CONSTRAINT fk_users_account FOREIGN KEY (account_id) REFERENCES account(id);