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
    public static final String GET_PASSENGER_RATING_LOG = "Get passenger rating with id {} ";
    public static final String RATE_PASSENGER_LOG = "Passenger with id {} got new grade, rating was updated";
    public static final String CREATE_NEW_PASSENGER_LOG = "Passenger with id {} created";
    public static final String UPDATE_PASSENGER_LOG = "Passenger with id {} updated";
    public static final String DELETE_PASSENGER_LOG = "Passenger with id {} deleted";
    public static final String GET_PASSENGER_LOG = "Get passenger with id {}";
    public static final String GET_PASSENGER_BY_PHONE_LOG = "Get passenger with phone {}";
    public static final String GET_ALL_PASSENGERS_LOG = "Get all passengers";
    public static final String GET_PAGEABLE_PASSENGERS_LOG = "Get all passengers(pageable)";

}
