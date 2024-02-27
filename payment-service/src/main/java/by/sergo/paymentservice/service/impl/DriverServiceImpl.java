package by.sergo.paymentservice.service.impl;

import by.sergo.paymentservice.client.DriverFeignClient;
import by.sergo.paymentservice.domain.dto.response.UserResponse;
import by.sergo.paymentservice.service.DriverService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static by.sergo.paymentservice.util.ConstantUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private final DriverFeignClient driverFeignClient;

    @CircuitBreaker(name = "driver", fallbackMethod = "getFallbackDriver")
    @Override
    public UserResponse getDriver(Long id) {
        return driverFeignClient.getDriverById(id);
    }

    private UserResponse getFallbackDriver(Long id, Exception ex) {
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
