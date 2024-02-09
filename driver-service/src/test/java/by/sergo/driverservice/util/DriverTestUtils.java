package by.sergo.driverservice.util;

import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.request.FindDriverForRideRequest;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.domain.entity.Driver;
import lombok.experimental.UtilityClass;

import static by.sergo.driverservice.domain.enums.Status.AVAILABLE;

@UtilityClass
public class DriverTestUtils {
    public static final Long DEFAULT_ID = 1L;
    public static final Long NOT_FOUND_ID = 2L;
    public static final String DEFAULT_NAME = "Petr";
    public static final String DEFAULT_SURNAME = "Petrov";
    public static final String DEFAULT_EMAIL = "petr@gmail.com";
    public static final String DEFAULT_PHONE = "+375331234567";
    public static final String NEW_EMAIL = "newpetr@gmail.com";
    public static final String NEW_PHONE = "+375331234565";
    public static final Double DEFAULT_RATING = 5.0;
    public static final Long DEFAULT_DRIVER_ID = 1L;
    public final int VALID_PAGE = 1;
    public final int VALID_SIZE = 1;
    public final String VALID_ORDER_BY = "name";
    public final int INVALID_PAGE = -1;
    public final int INVALID_SIZE = -1;
    public final String INVALID_ORDER_BY = "mane";
    public final String DEFAULT_STATUS = "AVAILABLE";
    public final String DEFAULT_RIDE_ID = "ride_id";

    public DriverCreateUpdateRequest getDefaultDriverRequest() {
        return DriverCreateUpdateRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public Driver getDefaultDriver() {
        return Driver.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .status(AVAILABLE)
                .build();
    }

    public Driver getSecondDriver() {
        return Driver.builder()
                .id(NOT_FOUND_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(NEW_EMAIL)
                .phone(NEW_PHONE)
                .rating(DEFAULT_RATING)
                .status(AVAILABLE)
                .build();
    }

    public Driver getNotSavedDriver() {
        return Driver.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public DriverResponse getDefaultDriverResponse() {
        return DriverResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .status(DEFAULT_STATUS)
                .build();
    }

    public DriverCreateUpdateRequest getDriverUpdateRequest() {
        return DriverCreateUpdateRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(NEW_PHONE)
                .email(NEW_EMAIL)
                .build();
    }


    public Driver getUpdatedDriver() {
        return Driver.builder()
                .id(DEFAULT_DRIVER_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(NEW_EMAIL)
                .phone(NEW_PHONE)
                .rating(DEFAULT_RATING)
                .status(AVAILABLE)
                .build();
    }

    public DriverResponse getUpdatedDriverResponse() {
        return DriverResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(NEW_EMAIL)
                .phone(NEW_PHONE)
                .rating(DEFAULT_RATING)
                .status(DEFAULT_STATUS)
                .build();
    }

    public FindDriverForRideRequest gerDefaultFindDriverForRideRequest() {
        return FindDriverForRideRequest.builder()
                .rideId(DEFAULT_RIDE_ID)
                .build();
    }
}
