CREATE TABLE IF NOT EXISTS car
(
    id                  BIGSERIAL PRIMARY KEY,
    model               VARCHAR(255) NOT NULL,
    year_of_manufacture INT          NOT NULL,
    number              VARCHAR(255) NOT NULL UNIQUE,
    color               VARCHAR(32)  NOT NULL,
    driver_id           BIGINT UNIQUE
);