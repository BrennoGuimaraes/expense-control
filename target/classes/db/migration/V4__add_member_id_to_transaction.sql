ALTER TABLE transaction
    ADD COLUMN member_id BIGINT NOT NULL,
ADD CONSTRAINT fk_transaction_member FOREIGN KEY (member_id) REFERENCES members(id);