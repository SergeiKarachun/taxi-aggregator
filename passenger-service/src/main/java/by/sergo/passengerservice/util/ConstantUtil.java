package by.sergo.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantUtil {
    public static final String PHONE_REGEX = "^\\+375(29|33|44|25)(\\d{7})$";
    public static final String EMAIL_EXAMPLE = "example@gmail.com";
    public static final Double DEFAULT_RATING = 5.0;
    public static final long RETRYER_PERIOD = 100L;
    public static final long RETRYER_MAX_PERIOD = 1000L;
    public static final int RETRYER_MAX_ATTEMPTS = 5;
    public static final String DEFAULT = "default";
    public static final String DEFAULT_EMAIL = "default@default.com";
    public static final String DEFAULT_PHONE = "+375290000000";
    public static final Long DEFAULT_ID = 0L;
    public static final Integer DEFAULT_CAR_YEAR = 0;


}
