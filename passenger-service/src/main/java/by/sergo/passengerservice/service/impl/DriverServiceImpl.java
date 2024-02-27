package by.sergo.passengerservice.service.impl;

import by.sergo.passengerservice.client.DriverFeignClient;
import by.sergo.passengerservice.domain.dto.response.CarResponse;
import by.sergo.passengerservice.domain.dto.response.DriverResponse;
import by.sergo.passengerservice.service.DriverService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static by.sergo.passengerservice.util.ConstantUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private final DriverFeignClient driverFeignClient;

    @CircuitBreaker(name = "driver", fallbackMethod = "getFallbackDriver")
    @Override
    public DriverResponse getDriver(Long id) {
        return driverFeignClient.getDriverById(id);
    }

    private DriverResponse getFallbackDriver(Long id, Exception ex) {
        log.error(ex.getMessage());
        return DriverResponse.builder()
                .id(id)
                .name(DEFAULT)
                .surname(DEFAULT)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .car(CarResponse.builder()
                        .id(DEFAULT_ID)
                        .model(DEFAULT)
                        .yearOfManufacture(DEFAULT_CAR_YEAR)
                        .number(DEFAULT)
                        .color(DEFAULT)
                        .driverId(DEFAULT_ID)
                        .build())
                .build();
    }
}
