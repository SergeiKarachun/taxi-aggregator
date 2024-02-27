package by.sergo.rideservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantUtil {
    public static final Double MIN_PRICE = 2.70;
    public static final Double MAX_PRICE = 30.0;
    public static final long RETRYER_PERIOD = 100L;
    public static final long RETRYER_MAX_PERIOD = 1000L;
    public static final int RETRYER_MAX_ATTEMPTS = 5;
    public static final String DEFAULT = "default";
    public static final String DEFAULT_EMAIL = "default@default.com";
    public static final String DEFAULT_PHONE = "+375290000000";
    public static final String DEFAULT_USER_TYPE = "PASSENGER";
    public static final Long DEFAULT_ID = 0L;
    public static final Integer DEFAULT_CAR_YEAR = 0;
    public static final Double DEFAULT_RATING = 5.0;
}
