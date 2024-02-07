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
    public final Long DEFAULT_ID = 1L;
    public final Long NOT_FOUND_ID = 2L;
    public final Long NOT_EXIST_ID = 99L;
    public final String DEFAULT_NAME = "Petr";
    public final String DEFAULT_SURNAME = "Petrov";
    public final String DEFAULT_EMAIL = "petr@gmail.com";
    public final String DEFAULT_PHONE = "+375331234567";
    public final Double DEFAULT_RATING = 5.0;
    public final String DEFAULT_MODEL = "Nissan";
    public final Integer DEFAULT_YEAR = 2023;
    public final String DEFAULT_NUMBER = "5885 AK-7";
    public final String NEW_NUMBER = "7777 AK-7";
    public final String DEFAULT_COLOR = "RED";
    public final Long DEFAULT_DRIVER_ID = 1L;
    public final int INVALID_PAGE = -1;
    public final int INVALID_SIZE = -1;
    public final String DEFAULT_PATH = "/api/v1/cars";
    public final String DEFAULT_ID_PATH = "/api/v1/cars/{id}";
    public final String DEFAULT_ID_PATH_BY_DRIVER = "/api/v1/cars/driver/{id}";
    public final String ID_PARAM_NAME = "id";
    public final int VALID_PAGE = 1;
    public final int VALID_SIZE = 1;
    public final String VALID_ORDER_BY = "model";
    public final String INVALID_ORDER_BY = "moled";
    public final String PAGE_PARAM_NAME = "page";
    public final String SIZE_PARAM_NAME = "size";
    public final String ORDER_BY_PARAM_NAME = "orderBy";

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
