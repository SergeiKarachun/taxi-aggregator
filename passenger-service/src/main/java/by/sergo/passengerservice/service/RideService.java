package by.sergo.passengerservice.service;

import by.sergo.passengerservice.domain.dto.response.RideResponse;

public interface RideService {

    RideResponse getRide(String id);
}
