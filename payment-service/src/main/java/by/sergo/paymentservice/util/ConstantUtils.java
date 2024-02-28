package by.sergo.paymentservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantUtils {
    public static final String CREDIT_CARD_NUMBER_PATTERN = "^\\d{16}$";
    public static final String CVV_PATTERN = "^\\d{3}$";
    public static final String USER_TYPE_PATTERN = "^DRIVER|PASSENGER$";
    public static final String MIN_BALANCE = "0.0";
    public static final String MIN_PRICE = "2.70";
    public static final long RETRYER_PERIOD = 100L;
    public static final long RETRYER_MAX_PERIOD = 1000L;
    public static final int RETRYER_MAX_ATTEMPTS = 5;
    public static final String DEFAULT = "default";
    public static final String DEFAULT_EMAIL = "default@default.com";
    public static final String DEFAULT_PHONE = "+375290000000";
    public static final Long DEFAULT_ID = 0L;
    public static final Double DEFAULT_RATING = 5.0;
    public static final String CREATE_ACCOUNT_LOG = "New account with id {} created";
    public static final String DELETE_ACCOUNT_LOG = "Account with id {} deleted";
    public static final String WITHDRAWAL_MONEY_FROM_ACCOUNT_LOG = "Withdrawal money from account with driverId {}, sum {}";
    public static final String GET_ACCOUNT_BY_DRIVER_ID_LOG = "Get account with driverId {}";
    public static final String GET_ACCOUNT_LOG = "Get account with id {}";
    public static final String CREATE_CREDIT_CARD_LOG = "Credit card with id {} created";
    public static final String GET_CREDIT_CARD_BY_DRIVER_ID_LOG = "Get credit card with driverId {}";
    public static final String GET_CREDIT_CARD_BY_PASSENGER_ID_LOG = "Get credit card with passengerId {}";
    public static final String GET_CREDIT_CARD_BY_ID_LOG = "Get credit card with id {}";
    public static final String CHANGE_CREDIT_CARD_LOG = "Credit card with driverId {} updated";
    public static final String DELETE_CREDIT_CARD_LOG = "Credit card with driverId {} deleted";
    public static final String MAKE_PAYMENT_LOG = "The ride with id {} paid";
    public static final String GET_DRIVER_TRANSACTION_LOG = "Get drivers transactions with driverId {}";
    public static final String GET_PASSENGER_TRANSACTION_LOG = "Get passengers transactions with passengerId {}";

}
