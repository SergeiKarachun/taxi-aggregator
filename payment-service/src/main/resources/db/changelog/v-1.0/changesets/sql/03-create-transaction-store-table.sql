CREATE TABLE IF NOT EXISTS transaction_store
(
    id                 BIGSERIAL PRIMARY KEY,
    credit_card_number VARCHAR(255) NOT NULL,
    account_number     VARCHAR(255) NOT NULL,
    operation_date     TIMESTAMP    NOT NULL,
    operation          VARCHAR(255) NOT NULL,
    value              numeric(10, 2)
);