package by.sergo.paymentservice.service.impl;

import by.sergo.paymentservice.client.PassengerFeignClient;
import by.sergo.paymentservice.domain.dto.response.UserResponse;
import by.sergo.paymentservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerFeignClient passengerFeignClient;

    @Override
    public UserResponse getPassenger(Long id) {
        return passengerFeignClient.getPassengerById(id);
    }
}
