CREATE TABLE IF NOT EXISTS credit_card
(
    id                 BIGSERIAL PRIMARY KEY,
    credit_card_number VARCHAR(255) NOT NULL UNIQUE,
    cvv                VARCHAR(32)  NOT NULL,
    exp_date           DATE         NOT NULL,
    user_id            BIGINT       NOT NULL,
    balance            numeric(10, 2),
    user_type          VARCHAR(255) NOT NULL,
    UNIQUE (user_id, user_type)
);