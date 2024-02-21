package by.sergo.passengerservice.util;

import by.sergo.passengerservice.controller.handler.ValidationExceptionResponse;
import by.sergo.passengerservice.domain.dto.request.PassengerCreateUpdateRequest;
import by.sergo.passengerservice.domain.dto.request.RatingCreateRequest;
import by.sergo.passengerservice.domain.dto.response.*;
import by.sergo.passengerservice.domain.entity.Passenger;
import by.sergo.passengerservice.domain.entity.Rating;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@UtilityClass
public class PassengerTestUtils {

    public static final Long DEFAULT_ID = 1L;
    public static final Long NOT_FOUND_ID = 99L;
    public static final String DEFAULT_NAME = "Petr";
    public static final String DEFAULT_SURNAME = "Petrov";
    public static final String DEFAULT_EMAIL = "petr@gmail.com";
    public static final String NEW_EMAIL = "petrNew@gmail.com";
    public static final String DEFAULT_PHONE = "+375331234567";
    public static final String NEW_PHONE = "+375331234561";
    public static final Double DEFAULT_RATING = 5.0;
    public final int INVALID_PAGE = -1;
    public final int VALID_PAGE = 1;
    public final int INVALID_SIZE = -1;
    public final int VALID_SIZE = 10;
    public final String INVALID_ORDER_BY = "mane";
    public final String VALID_ORDER_BY = "name";
    public final String DEFAULT_RIDE_ID = "65c11084a3c5564557a6e768";
    public final Long DEFAULT_DRIVER_ID = 1L;
    public final Integer DEFAULT_GRADE = 4;
    public final Double AVERAGE_GRADE = 4.5;
    public final String DEFAULT_STATUS = "AVAILABLE";
    public final String DEFAULT_ID_PATH = "/api/v1/passengers/{id}";
    public final String DEFAULT_PATH = "/api/v1/passengers";
    public final String RATING_PATH = "/api/v1/passengers/{id}/rating";
    public final String DEFAULT_DRIVER_PATH = "/api/v1/drivers/";
    public final String DEFAULT_RIDE_PATH = "/api/v1/rides/";
    public final String ID_PARAM_NAME = "id";
    public final Long NEW_ID = 4L;
    public final String UNIQUE_EMAIL = "unique@gmail.com";
    public final String UNIQUE_PHONE = "+375339876543";
    public final String PAGE_PARAM_NAME = "page";
    public final String SIZE_PARAM_NAME = "size";
    public final String ORDER_BY_PARAM_NAME = "orderBy";
    public final ResourceBundle validationMessages = ResourceBundle.getBundle("messages");
    public final Integer GRADE = 5;

    public PassengerResponse getDefaultPassengerResponse() {
        return PassengerResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public Passenger getPassengerToSave() {
        return Passenger.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public Passenger getDefaultPassenger() {
        return Passenger.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public PassengerCreateUpdateRequest getPassengerRequest() {
        return PassengerCreateUpdateRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public PassengerCreateUpdateRequest getUniquePassengerRequest() {
        return PassengerCreateUpdateRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .build();
    }

    public PassengerCreateUpdateRequest getPassengerUpdateRequest() {
        return PassengerCreateUpdateRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(NEW_EMAIL)
                .phone(NEW_PHONE)
                .build();
    }

    public PassengerResponse getUpdatePassengerResponse() {
        return PassengerResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(NEW_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public Passenger getUpdatedPassenger() {
        return Passenger.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(NEW_EMAIL)
                .phone(DEFAULT_PHONE)
                .rating(DEFAULT_RATING)
                .build();
    }

    public List<Passenger> getDefaultPassengersList() {
        Passenger second = Passenger.builder()
                .id(NOT_FOUND_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(NEW_EMAIL)
                .phone(NEW_PHONE)
                .rating(DEFAULT_RATING)
                .build();
        return List.of(getDefaultPassenger(), second);
    }

    public RatingCreateRequest getRatingRequest() {
        return RatingCreateRequest.builder()
                .grade(DEFAULT_GRADE)
                .driverId(DEFAULT_DRIVER_ID)
                .rideId(DEFAULT_RIDE_ID)
                .build();
    }

    public Rating getDefaulRating() {
        return Rating.builder()
                .passenger(getDefaultPassenger())
                .driverId(DEFAULT_DRIVER_ID)
                .rideId(DEFAULT_RIDE_ID)
                .grade(DEFAULT_GRADE)
                .build();
    }

    public PassengerRatingResponse getPassengerRatingResponse() {
        return PassengerRatingResponse.builder()
                .rating(AVERAGE_GRADE)
                .passengerId(DEFAULT_ID)
                .build();
    }


    public RatingResponse getDefaultRatingResponse() {
        return RatingResponse.builder()
                .passengerId(DEFAULT_ID)
                .driver(getDefaultDriverResponse())
                .ride(getDefaultRideResponse())
                .grade(DEFAULT_GRADE)
                .build();
    }

    public DriverResponse getDefaultDriverResponse() {
        return DriverResponse.builder()
                .id(DEFAULT_DRIVER_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(DEFAULT_PHONE)
                .email(DEFAULT_EMAIL)
                .status(DEFAULT_STATUS)
                .rating(DEFAULT_RATING)
                .build();
    }

    public RideResponse getDefaultRideResponse() {
        return RideResponse.builder()
                .id(DEFAULT_RIDE_ID)
                .passengerId(DEFAULT_ID)
                .driverId(DEFAULT_DRIVER_ID)
                .build();
    }

    public ValidationExceptionResponse getRatingValidationExceptionResponse() {
        String driverId = validationMessages.getString("min.value");
        String grade = validationMessages.getString("max.value");
        String rideId = validationMessages.getString("ride.id.not.blank");
        return ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Please check input parameters")
                .errors(Map.of(
                        "driverId", driverId,
                        "rideId", rideId,
                        "grade", grade
                ))
                .time(LocalDateTime.now())
                .build();
    }

    public static RatingResponse getRatingResponse() {
        return RatingResponse.builder()
                .passengerId(DEFAULT_ID)
                .grade(DEFAULT_GRADE)
                .build();
    }
}
