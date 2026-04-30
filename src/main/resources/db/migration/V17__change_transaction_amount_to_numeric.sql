ALTER TABLE transaction
    ALTER COLUMN amount TYPE NUMERIC(19,2)
    USING ROUND(amount::numeric, 2);
