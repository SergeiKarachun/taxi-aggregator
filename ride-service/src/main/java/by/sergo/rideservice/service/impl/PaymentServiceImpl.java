package by.sergo.rideservice.service.impl;

import by.sergo.rideservice.client.PaymentFeignClient;
import by.sergo.rideservice.domain.dto.response.CreditCardResponse;
import by.sergo.rideservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentFeignClient paymentFeignClient;

    @Override
    public CreditCardResponse getPassengerCreditCard(Long id) {
        return paymentFeignClient.getPassengerCreditCard(id);
    }
}
