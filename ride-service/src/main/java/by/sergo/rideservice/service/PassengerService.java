package by.sergo.rideservice.service;

import by.sergo.rideservice.domain.dto.response.PassengerResponse;

public interface PassengerService {
    PassengerResponse getPassenger(Long id);
}
