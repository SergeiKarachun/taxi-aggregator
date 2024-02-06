package by.sergo.rideservice.service;

import by.sergo.rideservice.domain.dto.response.DriverResponse;

public interface DriverService {
    DriverResponse getDriver(Long id);
}
