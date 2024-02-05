package by.sergo.paymentservice.service;

import by.sergo.paymentservice.domain.dto.response.UserResponse;

public interface DriverService {
    UserResponse getDriver(Long id);
}
