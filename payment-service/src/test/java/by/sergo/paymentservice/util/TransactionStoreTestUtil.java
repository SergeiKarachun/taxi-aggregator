package by.sergo.paymentservice.util;

import by.sergo.paymentservice.domain.dto.response.TransactionStoreResponse;
import by.sergo.paymentservice.domain.entity.Account;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static by.sergo.paymentservice.domain.enums.Operation.PAYMENT;
import static by.sergo.paymentservice.domain.enums.Operation.WITHDRAWAL;
import static by.sergo.paymentservice.domain.enums.UserType.DRIVER;

@UtilityClass
public class TransactionStoreTestUtil {

    public final Long DEFAULT_ID = 1L;
    public final Long NOT_EXIST = 99L;
    public final String DEFAULT_ACCOUNT_NUMBER = "95e6fe3a83c845c5b7a5bffe160e14a4";
    public final String DEFAULT_CREDIT_CARD_NUMBER = "2844575209195922";
    public final Long DEFAULT_DRIVER_ID = 1L;
    public final Long DEFAULT_PASSENGER_ID = 2L;
    public final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(10);
    public final String SORT_BY = "operationDate";
    public final int VALID_PAGE = 1;
    public final int VALID_SIZE = 1;
    public final int VALID_SIZE_TEN = 10;
    public final int INVALID_PAGE = -1;
    public final int INVALID_SIZE = -1;
    public final String PAGE_PARAM_NAME = "page";
    public final String SIZE_PARAM_NAME = "size";
    public final Long DEFAULT_CREDIT_CARD_ID = 1L;
    public final String CVV = "111";
    public final String ID_PARAM_NAME = "id";
    public final String DEFAULT_PASSENGER_ID_PATH = "/api/v1/transactions/passenger/{id}";
    public final String DEFAULT_DRIVER_ID_PATH = "/api/v1/transactions/driver/{id}";


    public Account getDriverAccount() {
        return Account.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .balance(DEFAULT_VALUE)
                .build();
    }

    public TransactionStore getDefaultWithdrawalTransaction() {
        return TransactionStore.builder()
                .id(DEFAULT_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .operationDate(LocalDateTime.now())
                .value(DEFAULT_VALUE)
                .operation(WITHDRAWAL)
                .build();
    }

    public TransactionStore getDefaultPaymentTransaction() {
        return TransactionStore.builder()
                .id(DEFAULT_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .operationDate(LocalDateTime.now())
                .value(DEFAULT_VALUE)
                .operation(PAYMENT)
                .build();
    }

    public TransactionStoreResponse getDefaultWithdrawalTransactionResponse() {
        return TransactionStoreResponse.builder()
                .id(DEFAULT_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .operationDate(LocalDateTime.now())
                .operation(WITHDRAWAL.name())
                .value(DEFAULT_VALUE)
                .build();
    }

    public TransactionStoreResponse getDefaultPaymentTransactionResponse() {
        return TransactionStoreResponse.builder()
                .id(DEFAULT_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .operationDate(LocalDateTime.now())
                .operation(PAYMENT.name())
                .value(DEFAULT_VALUE)
                .build();
    }

    public CreditCard getPassengerCreditCard() {
        return CreditCard.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_VALUE)
                .userId(DEFAULT_DRIVER_ID)
                .userType(DRIVER)
                .build();
    }
}
