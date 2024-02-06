package by.sergo.driverservice.util;

import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.domain.dto.response.*;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.domain.entity.Rating;
import lombok.experimental.UtilityClass;

import static by.sergo.driverservice.domain.enums.Status.AVAILABLE;

@UtilityClass
public class RatingTestUtils {
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_NAME = "Petr";
    public static final String DEFAULT_SURNAME = "Petrov";
    public static final String DEFAULT_EMAIL = "petr@gmail.com";
    public static final String DEFAULT_PHONE = "+375331234567";
    public static final Double DEFAULT_RATING = 5.0;
    public static final Double AVERAGE_GRADE = 4.5;
    public static final Long DEFAULT_DRIVER_ID = 1L;
    public static final Long DEFAULT_PASSENGER_ID = 1L;
    public static final Integer DEFAULT_GRADE = 5;
    public static final String DEFAULT_RIDE_ID = "ride_id";

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

    public RatingCreateRequest getRatingRequest() {
        return RatingCreateRequest.builder()
                .grade(DEFAULT_GRADE)
                .passengerId(DEFAULT_PASSENGER_ID)
                .rideId(DEFAULT_RIDE_ID)
                .build();
    }

    public Rating getDefaultRating() {
        return Rating.builder()
                .driver(getDefaultDriver())
                .passengerId(DEFAULT_PASSENGER_ID)
                .rideId(DEFAULT_RIDE_ID)
                .grade(DEFAULT_GRADE)
                .build();
    }

    public RatingResponse getDefaultRatingResponse() {
        return RatingResponse.builder()
                .id(DEFAULT_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .passenger(getDefaultPassengerResponse())
                .ride(getDefaultRideResponse())
                .grade(DEFAULT_GRADE)
                .build();
    }

    public RideResponse getDefaultRideResponse() {
        return RideResponse.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .build();
    }

    public PassengerResponse getDefaultPassengerResponse() {
        return PassengerResponse.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public DriverRatingResponse getDriverRatingResponse() {
        return DriverRatingResponse.builder()
                .rating(AVERAGE_GRADE)
                .driverId(DEFAULT_DRIVER_ID)
                .build();
    }
}
