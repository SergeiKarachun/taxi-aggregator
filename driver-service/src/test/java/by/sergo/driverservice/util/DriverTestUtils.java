package by.sergo.driverservice.util;

import by.sergo.driverservice.controller.handler.ValidationExceptionResponse;
import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.request.FindDriverForRideRequest;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.domain.dto.response.PassengerResponse;
import by.sergo.driverservice.domain.entity.Driver;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.ResourceBundle;

import static by.sergo.driverservice.domain.enums.Status.AVAILABLE;

@UtilityClass
public class DriverTestUtils {
    public static final Long DEFAULT_ID = 1L;
    public final Long NEW_ID = 4L;
    public final Long NOT_FOUND_ID = 2L;
    public final String DEFAULT_NAME = "petr";
    public final String DEFAULT_SURNAME = "petrov";
    public final String DEFAULT_EMAIL = "petr@gmail.com";
    public final String DEFAULT_PHONE = "+375331234567";
    public final String NEW_EMAIL = "newpet@gmail.com";
    public final String NEW_PHONE = "+375331111111";
    public final Double DEFAULT_RATING = 5.0;
    public final Long DEFAULT_DRIVER_ID = 1L;
    public final int VALID_PAGE = 1;
    public final int VALID_SIZE = 1;
    public final String VALID_ORDER_BY = "name";
    public final int INVALID_PAGE = -1;
    public final int INVALID_SIZE = -1;
    public final String INVALID_ORDER_BY = "mane";
    public final String DEFAULT_STATUS = "AVAILABLE";
    public final String DEFAULT_RIDE_ID = "ride_id";
    public final String DEFAULT_ID_PATH = "/api/v1/drivers/{id}";
    public final String DEFAULT_PATH = "/api/v1/drivers";
    public final String ID_PARAM = "id";
    public final ResourceBundle validationMessages = ResourceBundle.getBundle("messages");
    public final String PAGE_PARAM_NAME = "page";
    public final String SIZE_PARAM_NAME = "size";
    public final String ORDER_BY_PARAM_NAME = "orderBy";
    public final String AVAILABLE_PARAM = "/available";
    public final String CHANGE_STATUS_PATH = "/api/v1/drivers/{id}/status";

    public DriverCreateUpdateRequest getDefaultDriverRequest() {
        return DriverCreateUpdateRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(DEFAULT_EMAIL)
                .phone(DEFAULT_PHONE)
                .build();
    }

    public DriverCreateUpdateRequest getUniqueDriverRequest() {
        return DriverCreateUpdateRequest.builder()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(NEW_EMAIL)
                .phone(NEW_PHONE)
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

    public ValidationExceptionResponse getRatingValidationExceptionResponse() {
        String name = validationMessages.getString("name.not.blank");
        String surname = validationMessages.getString("surname.not.blank");
        String phone = validationMessages.getString("phone.not.blank");
        String email = validationMessages.getString("email.not.blank");
        return ValidationExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Please check input parameters")
                .errors(Map.of(
                        "name", name,
                        "surname", surname,
                        "phone", phone,
                        "email", email
                ))
                .time(LocalDateTime.now())
                .build();
    }
}
