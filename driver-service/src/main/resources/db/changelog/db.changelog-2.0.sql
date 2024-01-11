--liquibase formatted sql

--changeset Sergey Karachun:db.changelog-2.0 splitStatements:false logicalFilePath:classpath:/db/changelog/db.changelog-2.0.sql
--Car
CREATE TABLE IF NOT EXISTS car
(
    id                  BIGSERIAL PRIMARY KEY,
    model               VARCHAR(255) NOT NULL,
    year_of_manufacture INT          NOT NULL,
    number              VARCHAR(255) NOT NULL UNIQUE,
    color               VARCHAR(32)  NOT NULL,
    driver_id           BIGINT UNIQUE,
    CONSTRAINT car_driver_fk
        FOREIGN KEY (driver_id) REFERENCES driver (id)
            ON UPDATE CASCADE
            ON DELETE SET NULL
);
-- rollback drop table car cascade;
