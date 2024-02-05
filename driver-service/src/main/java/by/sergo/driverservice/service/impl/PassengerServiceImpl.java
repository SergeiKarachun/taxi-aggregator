package by.sergo.driverservice.service.impl;

import by.sergo.driverservice.client.PassengerFeignClient;
import by.sergo.driverservice.domain.dto.response.PassengerResponse;
import by.sergo.driverservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerFeignClient passengerFeignClient;

    @Override
    public PassengerResponse getPassenger(Long id) {
        return passengerFeignClient.getPassengerById(id);
    }
}
