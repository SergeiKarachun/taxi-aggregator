CREATE TABLE IF NOT EXISTS account
(
    id             BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    driver_id      BIGINT       NOT NULL UNIQUE,
    balance        numeric(10, 2)
);