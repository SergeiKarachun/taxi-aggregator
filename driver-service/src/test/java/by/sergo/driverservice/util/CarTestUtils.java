package by.sergo.driverservice.util;

import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.CarResponse;
import by.sergo.driverservice.domain.entity.Car;
import by.sergo.driverservice.domain.entity.Driver;
import lombok.experimental.UtilityClass;

import static by.sergo.driverservice.domain.enums.Color.*;
import static by.sergo.driverservice.domain.enums.Status.AVAILABLE;

@UtilityClass
public class CarTestUtils {
    public static final Long DEFAULT_ID = 1L;
    public static final Long NOT_FOUND_ID = 2L;
    public static final String DEFAULT_NAME = "Petr";
    public static final String DEFAULT_SURNAME = "Petrov";
    public static final String DEFAULT_EMAIL = "petr@gmail.com";
    public static final String DEFAULT_PHONE = "+375331234567";
    public static final Double DEFAULT_RATING = 5.0;
    public static final String DEFAULT_MODEL = "Nissan";
    public static final Integer DEFAULT_YEAR = 2023;
    public static final String DEFAULT_NUMBER = "5885 AK-7";
    public static final String NEW_NUMBER = "7777 AK-7";
    public static final String DEFAULT_COLOR = "RED";
    public static final Long DEFAULT_DRIVER_ID = 1L;
    public final int INVALID_PAGE = -1;
    public final int INVALID_SIZE = -1;
    public final String INVALID_ORDER_BY = "mane";

    public CarCreateUpdateRequest getCarCreateRequest() {
        return CarCreateUpdateRequest.builder()
                .model(DEFAULT_MODEL)
                .yearOfManufacture(DEFAULT_YEAR)
                .number(DEFAULT_NUMBER)
                .color(DEFAULT_COLOR)
                .driverId(DEFAULT_DRIVER_ID)
                .build();
    }

    public CarCreateUpdateRequest getCarUpdateRequest() {
        return CarCreateUpdateRequest.builder()
                .model(DEFAULT_MODEL)
                .yearOfManufacture(DEFAULT_YEAR)
                .number(NEW_NUMBER)
                .color(DEFAULT_COLOR)
                .driverId(DEFAULT_DRIVER_ID)
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

    public Car getCarToSave() {
        return Car.builder()
                .model(DEFAULT_MODEL)
                .yearOfManufacture(DEFAULT_YEAR)
                .number(DEFAULT_NUMBER)
                .color(RED)
                .driver(getDefaultDriver())
                .build();
    }

    public Car getDefaultCar() {
        return Car.builder()
                .id(DEFAULT_ID)
                .model(DEFAULT_MODEL)
                .yearOfManufacture(DEFAULT_YEAR)
                .number(DEFAULT_NUMBER)
                .color(RED)
                .driver(getDefaultDriver())
                .build();
    }

    public Car getUpdatedCar() {
        return Car.builder()
                .id(DEFAULT_ID)
                .model(DEFAULT_MODEL)
                .yearOfManufacture(DEFAULT_YEAR)
                .number(NEW_NUMBER)
                .color(RED)
                .driver(getDefaultDriver())
                .build();
    }

    public CarResponse getDefaultCarResponse() {
        return CarResponse.builder()
                .id(DEFAULT_ID)
                .model(DEFAULT_MODEL)
                .yearOfManufacture(DEFAULT_YEAR)
                .number(DEFAULT_NUMBER)
                .color(DEFAULT_COLOR)
                .driverId(DEFAULT_DRIVER_ID)
                .build();
    }
}
