package by.sergo.paymentservice.service;

import by.sergo.paymentservice.domain.dto.response.UserResponse;

public interface PassengerService {
    UserResponse getPassenger(Long id);
}
