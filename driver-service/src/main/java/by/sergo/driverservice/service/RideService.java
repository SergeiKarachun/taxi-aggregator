package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.response.RideResponse;

public interface RideService {
    RideResponse getRide(String id);
}
