package by.sergo.paymentservice.service.impl;

import by.sergo.paymentservice.client.DriverFeignClient;
import by.sergo.paymentservice.domain.dto.response.UserResponse;
import by.sergo.paymentservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverFeignClient driverFeignClient;

    @Override
    public UserResponse getDriver(Long id) {
        return driverFeignClient.getDriverById(id);
    }
}
