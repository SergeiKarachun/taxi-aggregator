--liquibase formatted sql

--changeset Sergey Karachun:db.changelog-1.0 splitStatements:false logicalFilePath:classpath:/db/changelog/db.changelog-1.0.sql
--Passenger
CREATE TABLE IF NOT EXISTS passenger
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(32) NOT NULL UNIQUE,
    rating DECIMAL
);
-- rollback drop table passenger;

--Rating
CREATE TABLE IF NOT EXISTS rating
(
    id BIGSERIAL PRIMARY KEY,
    grade INT NOT NULL,
    passenger_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    CONSTRAINT rating_passenger_fk
        FOREIGN KEY
            (
             passenger_id
                ) REFERENCES passenger
            (
             id
                )
            ON UPDATE CASCADE
            ON DELETE SET NULL
);
-- rollback drop table rating;



