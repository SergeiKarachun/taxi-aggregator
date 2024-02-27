package by.sergo.driverservice.service.impl;

import by.sergo.driverservice.client.RideFeignClient;
import by.sergo.driverservice.domain.dto.response.RideResponse;
import by.sergo.driverservice.domain.enums.RideStatus;
import by.sergo.driverservice.service.RideService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static by.sergo.driverservice.util.ConstantUtil.DEFAULT;
import static by.sergo.driverservice.util.ConstantUtil.DEFAULT_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideServiceImpl implements RideService {

    private final RideFeignClient rideFeignClient;

    @CircuitBreaker(name = "ride", fallbackMethod = "getFallbackRide")
    @Override
    public RideResponse getRide(String id) {
        return rideFeignClient.getRideById(id);
    }

    private RideResponse getFallbackRide(String id, Exception ex) {
        log.error(ex.getMessage());
        return RideResponse.builder()
                .id(id)
                .pickUpAddress(DEFAULT)
                .destinationAddress(DEFAULT)
                .creatingTime(LocalDateTime.now())
                .passengerId(DEFAULT_ID)
                .status(RideStatus.CREATED)
                .build();
    }
}
