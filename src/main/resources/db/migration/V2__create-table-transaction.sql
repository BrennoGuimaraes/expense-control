CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              description VARCHAR(255),
                              amount FLOAT8 NOT NULL,
                              type VARCHAR(50) NOT NULL,
                              date TIMESTAMP NOT NULL
);