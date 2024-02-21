package by.sergo.driverservice.integration.controller;

import by.sergo.driverservice.controller.handler.RestErrorResponse;
import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.CarResponse;
import by.sergo.driverservice.domain.entity.Car;
import by.sergo.driverservice.integration.config.IntegrationTestConfig;
import by.sergo.driverservice.mapper.CarMapper;
import by.sergo.driverservice.repository.CarRepository;
import by.sergo.driverservice.repository.DriverRepository;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static by.sergo.driverservice.util.CarTestUtils.*;
import static by.sergo.driverservice.util.ExceptionMessageUtil.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/insert-test_values-in-driver-table.sql",
                "classpath:sql/insert-test_values-in-car-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/truncate-driver-table.sql",
                "classpath:sql/truncate-car-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CarIntegrationTest extends IntegrationTestConfig {
    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final CarMapper carMapper;
    @LocalServerPort
    private int port;

    @Test
    void addCar_whenDataIsValidAndUnique() {
        CarCreateUpdateRequest createRequest = getCarUpdateRequest();
        createRequest.setDriverId(3L);

        CarResponse expected = CarResponse.builder()
                .id(3L)
                .model(DEFAULT_MODEL)
                .yearOfManufacture(DEFAULT_YEAR)
                .number(NEW_NUMBER)
                .color(DEFAULT_COLOR)
                .driverId(3L)
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CarResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addCar_whenDataNotUnique() {
        CarCreateUpdateRequest createRequest = getCarUpdateRequest();
        createRequest.setDriverId(NOT_EXIST_ID);
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(getNotFoundMessage("Driver", "driverId", NOT_EXIST_ID)))
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addCar_whenDriverAlreadyHasCar() {
        CarCreateUpdateRequest createRequest = getCarUpdateRequest();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(getAlreadyExistMessage("Car", "driverId", createRequest.getDriverId().toString())))
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateById_whenCarNotExist() {
        CarCreateUpdateRequest updateRequest = getCarUpdateRequest();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Car", "id", NOT_EXIST_ID)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_EXIST_ID)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateById_whenDataIsValidAndUnique() {
        CarCreateUpdateRequest updateRequest = getCarUpdateRequest();
        CarResponse expected = CarResponse.builder()
                .id(DEFAULT_ID)
                .model(DEFAULT_MODEL)
                .yearOfManufacture(DEFAULT_YEAR)
                .number(NEW_NUMBER)
                .color(DEFAULT_COLOR)
                .driverId(DEFAULT_DRIVER_ID)
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .body(updateRequest)
                .when()
                .put(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById_whenCarNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Car", "id", NOT_EXIST_ID)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_EXIST_ID)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById_whenCarExists() {
        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    @Test
    void findById_whenCarNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Car", "id", NOT_EXIST_ID)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_EXIST_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findById_whenDriverExists() {
        Car car = carRepository.findById(DEFAULT_ID).get();
        CarResponse expected = carMapper.mapToDto(car);

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarResponse.class);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void findCarByDriverId_whenDriverNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Car", "driverId", NOT_EXIST_ID)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_EXIST_ID)
                .when()
                .get(DEFAULT_ID_PATH_BY_DRIVER)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findCarByDriverId_whenDriverExists() {
        Car car = carRepository.getCarByDriverId(DEFAULT_ID).get();
        CarResponse expected = carMapper.mapToDto(car);

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(DEFAULT_ID_PATH_BY_DRIVER)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CarResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAll_whenParamsValid() {
        Page<Car> carPage = carRepository.findAll(
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY))
        );
        List<CarResponse> expected = carPage.stream()
                .map(carMapper::mapToDto)
                .toList();

        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("cars", CarResponse.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(carRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    void findAll_whenInvalidPage() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(getInvalidRequestMessage(INVALID_PAGE, VALID_SIZE)))
                .build();

        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, INVALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
