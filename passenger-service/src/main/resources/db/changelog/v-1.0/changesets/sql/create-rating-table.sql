CREATE TABLE IF NOT EXISTS rating
(
    id BIGSERIAL PRIMARY KEY,
    grade INT NOT NULL,
    passenger_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    ride_id BIGINT NOT NULL UNIQUE
);
