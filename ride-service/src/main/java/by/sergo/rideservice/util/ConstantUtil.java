package by.sergo.rideservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantUtil {
    public static final Double MIN_PRICE = 2.70;
    public static final Double MAX_PRICE = 30.0;
    public static final long RETRYER_PERIOD = 100L;
    public static final long RETRYER_MAX_PERIOD = 1000L;
    public static final int RETRYER_MAX_ATTEMPTS = 5;
}
