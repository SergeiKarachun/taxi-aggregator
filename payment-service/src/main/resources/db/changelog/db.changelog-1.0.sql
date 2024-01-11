--liquibase formatted sql

--changeset Sergey Karachun:db.changelog-1.0 splitStatements:false logicalFilePath:classpath:/db/changelog/db.changelog-1.0.sql
--account
CREATE TABLE IF NOT EXISTS account
(
    id             BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    driver_id      BIGINT       NOT NULL UNIQUE,
    balance        numeric(10, 2)
);
-- rollback drop table account;

--credit_card
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
-- rollback drop table credit_card;

CREATE TABLE IF NOT EXISTS transaction_store
(
    id                 BIGSERIAL PRIMARY KEY,
    credit_card_number VARCHAR(255) NOT NULL,
    account_number     VARCHAR(255) NOT NULL,
    operation_date     TIMESTAMP    NOT NULL,
    operation          VARCHAR(255) NOT NULL,
    value              numeric(10, 2)
);
-- rollback drop table transaction_store;
