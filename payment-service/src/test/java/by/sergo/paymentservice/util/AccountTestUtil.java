package by.sergo.paymentservice.util;

import by.sergo.paymentservice.domain.dto.request.AccountCreateUpdateRequest;
import by.sergo.paymentservice.domain.dto.response.AccountResponse;
import by.sergo.paymentservice.domain.entity.Account;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

import static by.sergo.paymentservice.domain.enums.Operation.*;
import static by.sergo.paymentservice.domain.enums.UserType.*;

@UtilityClass
public class AccountTestUtil {

    public final Long DEFAULT_ID = 1L;
    public final Long NOT_EXIST_ID = 99L;
    public final String DEFAULT_ACCOUNT_NUMBER = "95e6fe3a83c845c5b7a5bffe160e14a4";
    public final Long DEFAULT_DRIVER_ID = 1L;
    public final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(10);
    public final BigDecimal BALANCE_AFTER_WITHDRAWAL = BigDecimal.valueOf(20);
    public final BigDecimal ZERO_BALANCE = BigDecimal.ZERO;
    public final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(10);
    public final Long DEFAULT_CREDIT_CARD_ID = 1L;
    public final String DEFAULT_CREDIT_CARD_NUMBER = "2844575209195922";
    public final String CVV = "111";
    public final String DEFAULT_PATH = "/api/v1/accounts";
    public final String DEFAULT_ID_PATH = "/api/v1/accounts/{id}";
    public final String DEFAULT_DRIVER_ID_PATH = "/api/v1/accounts/driver/{id}";
    public final String ID_PARAM_NAME = "id";
    public final String WITHDRAWAL_PARAM = "sum";
    public final BigDecimal WITHDRAWAL_SUM= BigDecimal.valueOf(10);

    public AccountCreateUpdateRequest getAccountCreateRequest() {
        return AccountCreateUpdateRequest.builder()
                .driverId(DEFAULT_DRIVER_ID)
                .balance(DEFAULT_BALANCE)
                .build();
    }

    public AccountResponse getAccountResponse() {
        return AccountResponse.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .balance(DEFAULT_BALANCE)
                .build();
    }

    public Account getAccount() {
        return Account.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .balance(DEFAULT_BALANCE)
                .build();
    }

    public Account getAccountAfterWithdrawal() {
        return Account.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .balance(ZERO_BALANCE)
                .build();
    }

    public CreditCard getCreditCard() {
        return CreditCard.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .userId(DEFAULT_DRIVER_ID)
                .userType(DRIVER)
                .build();
    }

    public CreditCard getCreditCardAfterWithdrawal() {
        return CreditCard.builder()
                .id(DEFAULT_CREDIT_CARD_ID)
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .cvv(CVV)
                .balance(BALANCE_AFTER_WITHDRAWAL)
                .userId(DEFAULT_DRIVER_ID)
                .userType(DRIVER)
                .build();
    }
    public TransactionStore getTransaction() {
        return TransactionStore.builder()
                .creditCardNumber(DEFAULT_CREDIT_CARD_NUMBER)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .operation(WITHDRAWAL)
                .value(DEFAULT_VALUE)
                .build();
    }

    public static AccountResponse getAccountResponseAfterWithdrawal() {
        return AccountResponse.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .accountNumber(DEFAULT_ACCOUNT_NUMBER)
                .balance(ZERO_BALANCE)
                .build();
    }
}
