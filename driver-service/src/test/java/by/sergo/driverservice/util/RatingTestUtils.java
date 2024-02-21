package by.sergo.driverservice.util;

import by.sergo.driverservice.controller.handler.ValidationExceptionResponse;
import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.domain.dto.response.DriverRatingResponse;
import by.sergo.driverservice.domain.dto.response.PassengerResponse;
import by.sergo.driverservice.domain.dto.response.RatingResponse;
import by.sergo.driverservice.domain.dto.response.RideResponse;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.domain.entity.Rating;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.ResourceBundle;

import static by.sergo.driverservice.domain.enums.Status.AVAILABLE;

@UtilityClass
public class RatingTestUtils {
    public final Long DEFAULT_ID = 1L;
    public final String DEFAULT_NAME = "Petr";
    public final String DEFAULT_SURNAME = "Petrov";
    public final String DEFAULT_EMAIL = "petr@gmail.com";
    public final String DEFAULT_PHONE = "+375331234567";
    public final Double DEFAULT_RATING = 5.0;
    public final Double AVERAGE_GRADE = 4.5;
    public final Long DEFAULT_DRIVER_ID = 1L;
    public final Long DEFAULT_PASSENGER_ID = 1L;
    public final Integer DEFAULT_GRADE = 5;
    public final String DEFAULT_RIDE_ID = "65c11084a3c5564557a6e768";
    public final String ID_PARAM_NAME = "id";
    public final String RATING_PATH = "/api/v1/drivers/{id}/rating";
    public final String DEFAULT_PASSENGER_PATH = "/api/v1/passengers/";
    public final String DEFAULT_RIDE_PATH = "/api/v1/rides/";
    public final ResourceBundle validationMessages = ResourceBundle.getBundle("messages");

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

    public RatingResponse getRatingResponse() {
        return RatingResponse.builder()
                .driverId(DEFAULT_DRIVER_ID)
                .grade(DEFAULT_GRADE)
                .build();
    }

    public ValidationExceptionResponse getRatingValidationExceptionResponse() {
        String passengerId = validationMessages.getString("min.value");
        String grade = validationMessages.getString("max.value");
        String rideId = validationMessages.getString("ride.id.not.blank");
        return ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Please check input parameters")
                .errors(Map.of(
                        "passengerId", passengerId,
                        "rideId", rideId,
                        "grade", grade
                ))
                .time(LocalDateTime.now())
                .build();
    }
}
