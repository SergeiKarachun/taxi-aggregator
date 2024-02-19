package by.sergo.rideservice.service.impl;

import by.sergo.rideservice.client.PassengerFeignClient;
import by.sergo.rideservice.domain.dto.response.PassengerResponse;
import by.sergo.rideservice.service.PassengerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static by.sergo.rideservice.util.ConstantUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerFeignClient passengerFeignClient;

    @CircuitBreaker(name = "passenger", fallbackMethod = "getFallbackPassenger")
    @Override
    public PassengerResponse getPassenger(Long id) {
        return passengerFeignClient.getPassengerById(id);
    }

    private PassengerResponse getFallbackPassenger(Long id, Exception ex) {
        log.error(ex.getMessage());
        return PassengerResponse.builder()
                .id(id)
                .name(DEFAULT)
                .surname(DEFAULT)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }
}
