package by.sergo.paymentservice.util;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.request.PaymentRequest;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;
import by.sergo.paymentservice.domain.dto.response.UserResponse;
import by.sergo.paymentservice.domain.entity.Account;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;

import static by.sergo.paymentservice.domain.enums.Operation.PAYMENT;
import static by.sergo.paymentservice.domain.enums.UserType.DRIVER;
import static by.sergo.paymentservice.domain.enums.UserType.PASSENGER;

@UtilityClass
public class CreditCardTestUtil {
    public final Long DEFAULT_ID = 1L;
    public final String DEFAULT_ACCOUNT_NUMBER = "95e6fe3a83c845c5b7a5bffe160e14a4";
    public final Long DEFAULT_DRIVER_ID = 1L;
    public final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(10);
    public final BigDecimal BALANCE_AFTER_PAYMENT = BigDecimal.valueOf(20);
    public final BigDecimal ZERO_BALANCE = BigDecimal.ZERO;
    public final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(10);
    public final Long DEFAULT_CREDIT_CARD_ID = 1L;
    public final String DEFAULT_CREDIT_CARD_NUMBER = "2844575209195922";
    public final String NEW_CREDIT_CARD_NUMBER = "2844575209195911";
    public final String CVV = "111";
    public final String DEFAULT_NAME = "Petr";
    public final String DEFAULT_SURNAME = "Petrov";
    public final String DEFAULT_EMAIL = "petr@gmail.com";
    public final String DEFAULT_PHONE = "+375331234567";
    public final Double DEFAULT_RATING = 5.0;
    public final String DEFAULT_RIDE_ID = "ride_id";

    public Account getAccount() {
        return Account.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .balance(DEFAULT_BALANCE)
                .build();
    }

    public CreditCardCreateUpdate getCreditCardRequest() {
        return CreditCardCreateUpdate.builder()
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .userId(DEFAULT_DRIVER_ID)
                .userType(DRIVER.name())
                .build();
    }

    public CreditCardCreateUpdate getCreditCardUpdateRequest() {
        return CreditCardCreateUpdate.builder()
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .userId(DEFAULT_DRIVER_ID)
                .userType(DRIVER.name())
                .build();
    }

    public CreditCard getCreditCard() {
        return CreditCard.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .userId(DEFAULT_DRIVER_ID)
                .userType(DRIVER)
                .build();
    }

    public CreditCard getCreditCardAfterPayment() {
        return CreditCard.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(ZERO_BALANCE)
                .userId(DEFAULT_ID)
                .userType(PASSENGER)
                .build();
    }

    public CreditCard getUpdatedCreditCard() {
        return CreditCard.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .userId(DEFAULT_DRIVER_ID)
                .userType(DRIVER)
                .build();
    }

    public CreditCardResponse getCreditCardResponse() {
        return CreditCardResponse.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .user(getUserResponse())
                .userType(DRIVER.name())
                .build();
    }

    public CreditCardResponse getCreditCardAfterPaymentResponse() {
        return CreditCardResponse.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(ZERO_BALANCE)
                .user(getUserResponse())
                .userType(PASSENGER.name())
                .build();
    }
    public CreditCardResponse getUpdatedCreditCardResponse() {
        return CreditCardResponse.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .user(getUserResponse())
                .userType(DRIVER.name())
                .build();
    }

    public UserResponse getUserResponse() {
        return UserResponse.builder()
                .id(DEFAULT_DRIVER_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public TransactionStore getTransaction() {
        return TransactionStore.builder()
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .operation(PAYMENT)
                .value(DEFAULT_VALUE)
                .build();
    }

    public Account getAccountAfterPayment() {
        return Account.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .balance(BALANCE_AFTER_PAYMENT)
                .build();
    }

    public PaymentRequest getPaymentRequest() {
        return PaymentRequest.builder()
                .driverId(DEFAULT_DRIVER_ID)
                .passengerId(DEFAULT_ID)
                .rideId(DEFAULT_RIDE_ID)
                .sum(DEFAULT_VALUE)
                .build();
    }
}
