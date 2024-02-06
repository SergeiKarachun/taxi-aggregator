package by.sergo.rideservice.service.impl;

import by.sergo.rideservice.client.DriverFeignClient;
import by.sergo.rideservice.domain.dto.response.DriverResponse;
import by.sergo.rideservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverFeignClient driverFeignClient;

    @Override
    public DriverResponse getDriver(Long id) {
        return driverFeignClient.getDriverById(id);
    }
}
