package by.sergo.passengerservice.service.impl;

import by.sergo.passengerservice.client.RideFeignClient;
import by.sergo.passengerservice.domain.dto.response.RideResponse;
import by.sergo.passengerservice.service.RideService;
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
