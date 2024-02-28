package by.sergo.driverservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantUtil {
    public static final String NUMBER_PATTERN = "^\\d{4} [A-Z]{2}-\\d{1}$";
    public static final String COLOR_PATTERN = "^(RED|BLACK|WHITE|BLUE|SILVER|YELLOW|GREEN)$";
    public static final String EMAIL_EXAMPLE = "example@gmail.com";
    public static final String PHONE_PATTERN = "^\\+375(29|33|44|25)(\\d{7})$";
    public static final Double DEFAULT_RATING = 5.0;
    public static final long RETRYER_PERIOD = 100L;
    public static final long RETRYER_MAX_PERIOD = 1000L;
    public static final int RETRYER_MAX_ATTEMPTS = 5;
    public static final String DEFAULT = "default";
    public static final String DEFAULT_EMAIL = "default@default.com";
    public static final String DEFAULT_PHONE = "+375290000000";
    public static final Long DEFAULT_ID = 0L;
    public static final String CREATE_DRIVER_LOG = "Driver with id {} created";
    public static final String UPDATE_DRIVER_LOG = "Driver with id {} updated";
    public static final String DELETE_DRIVER_LOG = "Driver with id {} deleted";
    public static final String GET_DRIVER_LOG = "Get driver with id {}";
    public static final String GET_AVAILABLE_DRIVERS_LOG = "Get all available drivers";
    public static final String GET_DRIVERS_LOG = "Get all drivers";
    public static final String UPDATE_DRIVER_STATUS_LOG = "Driver with id {} changed status";
    public static final String FIND_DRIVER_FOR_RIDE_LOG = "Find driver with id {} for ride";
    public static final String CREATE_CAR_LOG = "Car with id {} created";
    public static final String UPDATE_CAR_LOG = "Car with id {} updated";
    public static final String DELETE_CAR_LOG = "Car with id {} deleted";
    public static final String GET_CAR_LOG = "Get car with id {}";
    public static final String GET_CAR_BY_DRIVER_ID_LOG = "Get car with driverId {}";
    public static final String GET_CARS_LOG = "Get all cars";
    public static final String RATE_DRIVER_LOG = "Driver with id {} got new grade, rating was updated";
    public static final String GET_DRIVER_RATING = "Get driver rating with id {} ";

}
