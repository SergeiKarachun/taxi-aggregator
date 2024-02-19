package by.sergo.rideservice.service.impl;

import by.sergo.rideservice.client.PaymentFeignClient;
import by.sergo.rideservice.domain.dto.response.CreditCardResponse;
import by.sergo.rideservice.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static by.sergo.rideservice.util.ConstantUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentFeignClient paymentFeignClient;

    @CircuitBreaker(name = "payment", fallbackMethod = "getFallbackPassengerCreditCard")
    @Override
    public CreditCardResponse getPassengerCreditCard(Long id) {
        return paymentFeignClient.getPassengerCreditCard(id);
    }

    private CreditCardResponse getFallbackPassengerCreditCard(Long id, Exception ex) {
        log.error(ex.getMessage());
        return CreditCardResponse.builder()
                .id(DEFAULT_ID)
                .creditCardNumber(DEFAULT)
                .cvv(DEFAULT)
                .userId(id)
                .balance(BigDecimal.ZERO)
                .userType(DEFAULT_USER_TYPE)
                .build();
    }
}
