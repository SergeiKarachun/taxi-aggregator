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