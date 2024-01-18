ALTER TABLE rating
ADD CONSTRAINT rating_driver_fk
    FOREIGN KEY (driver_id) REFERENCES driver(id)
    ON UPDATE CASCADE
       ON DELETE CASCADE