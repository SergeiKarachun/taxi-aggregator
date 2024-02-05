package by.sergo.passengerservice.service;

import by.sergo.passengerservice.domain.dto.response.DriverResponse;

public interface DriverService {
    DriverResponse getDriver(Long id);
}
