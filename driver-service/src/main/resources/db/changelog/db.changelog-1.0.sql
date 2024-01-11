--liquibase formatted sql

--changeset Sergey Karachun:db.changelog-1.0 splitStatements:false logicalFilePath:classpath:/db/changelog/db.changelog-1.0.sql
--Driver
CREATE TABLE IF NOT EXISTS driver
(
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email   VARCHAR(255) NOT NULL UNIQUE,
    phone   VARCHAR(32)  NOT NULL UNIQUE,
    status  varchar(255) NOT NULL,
    rating  DECIMAL
);
-- rollback drop table driver;

--Rating
CREATE TABLE IF NOT EXISTS rating
(
    id           BIGSERIAL PRIMARY KEY,
    grade        INT    NOT NULL,
    driver_id    BIGINT NOT NULL,
    passenger_id BIGINT NOT NULL,
    CONSTRAINT rating_driver_fk
        FOREIGN KEY (driver_id) REFERENCES driver (id)
            ON UPDATE CASCADE
            ON DELETE SET NULL
);
-- rollback drop table rating;



