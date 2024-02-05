package by.sergo.rideservice.service.impl;

import by.sergo.rideservice.client.PassengerFeignClient;
import by.sergo.rideservice.domain.dto.response.PassengerResponse;
import by.sergo.rideservice.service.PassengerService;
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
