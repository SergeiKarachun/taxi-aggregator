package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.response.PassengerResponse;

public interface PassengerService {
    PassengerResponse getPassenger(Long id);
}
