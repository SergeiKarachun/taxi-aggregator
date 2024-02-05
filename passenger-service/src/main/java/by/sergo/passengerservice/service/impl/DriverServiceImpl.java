package by.sergo.passengerservice.service.impl;

import by.sergo.passengerservice.client.DriverFeignClient;
import by.sergo.passengerservice.domain.dto.response.DriverResponse;
import by.sergo.passengerservice.service.DriverService;
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
