ALTER TABLE transaction
    ADD COLUMN category_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES categories(id);