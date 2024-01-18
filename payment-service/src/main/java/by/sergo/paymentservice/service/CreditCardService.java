package by.sergo.paymentservice.service;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.request.PaymentRequest;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;

public interface CreditCardService {
    CreditCardResponse addCard(CreditCardCreateUpdate dto);

    CreditCardResponse changeCard(CreditCardCreateUpdate dto, Long id);

    CreditCardResponse deleteById(Long id);

    CreditCardResponse getDriverCard(Long driverId);

    CreditCardResponse getPassengerCard(Long passengerId);

    CreditCardResponse makePayment(PaymentRequest payment);
}
