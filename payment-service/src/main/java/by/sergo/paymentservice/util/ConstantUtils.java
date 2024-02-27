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

}
