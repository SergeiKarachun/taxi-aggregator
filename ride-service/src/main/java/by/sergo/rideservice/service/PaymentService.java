package by.sergo.rideservice.service;

import by.sergo.rideservice.domain.dto.response.CreditCardResponse;

public interface PaymentService {
    CreditCardResponse getPassengerCreditCard(Long id);
}
