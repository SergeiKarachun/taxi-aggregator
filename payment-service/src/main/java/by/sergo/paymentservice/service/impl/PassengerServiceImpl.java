package by.sergo.paymentservice.service.impl;

import by.sergo.paymentservice.client.PassengerFeignClient;
import by.sergo.paymentservice.domain.dto.response.UserResponse;
import by.sergo.paymentservice.service.PassengerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static by.sergo.paymentservice.util.ConstantUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerFeignClient passengerFeignClient;

    @CircuitBreaker(name = "passenger", fallbackMethod = "getFallbackPassenger")
    @Override
    public UserResponse getPassenger(Long id) {
        return passengerFeignClient.getPassengerById(id);
    }

    private UserResponse getFallbackPassenger(Long id, Exception ex) {
        log.error(ex.getMessage());
        return UserResponse.builder()
                .id(id)
                .name(DEFAULT)
                .surname(DEFAULT)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }
}
