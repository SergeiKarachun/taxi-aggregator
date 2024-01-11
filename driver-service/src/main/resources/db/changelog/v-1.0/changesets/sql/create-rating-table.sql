CREATE TABLE IF NOT EXISTS rating
(
    id           BIGSERIAL PRIMARY KEY,
    grade        INT    NOT NULL,
    driver_id    BIGINT,
    passenger_id BIGINT NOT NULL,
    ride_id BIGINT UNIQUE NOT NULL
);