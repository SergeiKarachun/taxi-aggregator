package by.sergo.driverservice.service.impl;

import by.sergo.driverservice.client.RideFeignClient;
import by.sergo.driverservice.domain.dto.response.RideResponse;
import by.sergo.driverservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideFeignClient rideFeignClient;
    @Override
    public RideResponse getRide(String id) {
        return rideFeignClient.getRideById(id);
    }
}
