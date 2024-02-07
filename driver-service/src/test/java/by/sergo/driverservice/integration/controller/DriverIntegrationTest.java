package by.sergo.driverservice.integration.controller;

import by.sergo.driverservice.controller.handler.RestErrorResponse;
import by.sergo.driverservice.controller.handler.ValidationExceptionResponse;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.util.DriverTestUtils;
import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.domain.enums.Status;
import by.sergo.driverservice.integration.config.IntegrationTestConfig;
import by.sergo.driverservice.mapper.DriverMapper;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.util.ExceptionMessageUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static by.sergo.driverservice.util.DriverTestUtils.*;
import static by.sergo.driverservice.util.ExceptionMessageUtil.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/insert-test_values-in-driver-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/truncate-driver-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DriverIntegrationTest extends IntegrationTestConfig {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    @LocalServerPort
    private int port;

    @Test
    void addDriver_whenDataIsValidAndUnique() {
        DriverCreateUpdateRequest createRequest = getUniqueDriverRequest();

        DriverResponse expected = DriverResponse.builder()
                .id(NEW_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(NEW_PHONE)
                .email(NEW_EMAIL)
                .rating(DEFAULT_RATING)
                .status(Status.AVAILABLE.name())
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
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addDriver_whenDataNotUnique() {
        DriverCreateUpdateRequest createRequest = getDefaultDriverRequest();
        HashMap<String, String> errors = new HashMap<>();
        errors.put("email", getAlreadyExistMessage("Driver", "email", createRequest.getEmail()));
        errors.put("phone", getAlreadyExistMessage("Driver", "phone", createRequest.getPhone()));

        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(getAlreadyExistMapMessage(errors)))
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
    void addDriver_whenDataNotValid() {
        DriverCreateUpdateRequest invalidRequest = DriverCreateUpdateRequest.builder()
                .name(null)
                .surname(null)
                .phone(null)
                .build();
        ValidationExceptionResponse expected = getRatingValidationExceptionResponse();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateById_whenDriverNotExist() {
        DriverCreateUpdateRequest updateRequest = getUniqueDriverRequest();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Driver", "id", 99L)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, 99)
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
        DriverCreateUpdateRequest updateRequest = getUniqueDriverRequest();
        DriverResponse expected = DriverResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .phone(NEW_PHONE)
                .email(NEW_EMAIL)
                .rating(DEFAULT_RATING)
                .status(Status.AVAILABLE.name())
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAM, DEFAULT_ID)
                .body(updateRequest)
                .when()
                .put(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateById_whenDataNotUnique() {
        DriverCreateUpdateRequest updateRequest = getDefaultDriverRequest();
        HashMap<String, String> errors = new HashMap<>();
        errors.put("email", getAlreadyExistMessage("Driver", "email", updateRequest.getEmail()));
        errors.put("phone", getAlreadyExistMessage("Driver", "phone", updateRequest.getPhone()));

        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(getAlreadyExistMapMessage(errors)))
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAM, 2L)
                .body(updateRequest)
                .when()
                .put(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById_whenDriverNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Driver", "id", 99L)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, 99L)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById_whenDriverExists() {
        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, DEFAULT_ID)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    @Test
    void findById_whenDriverNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Driver", "id", 99L)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, 99L)
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
        Driver driver = driverRepository.findById(DEFAULT_ID).get();
        DriverResponse expected = driverMapper.mapToDto(driver);

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, DEFAULT_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAll_whenParamsValid() {
        Page<Driver> driverPage = driverRepository.findAll(
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY))
        );
        List<DriverResponse> expected = driverPage.stream()
                .map(driverMapper::mapToDto)
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
                .extract().body().jsonPath().getList("drivers", DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(driverRepository.findAll().size()).isEqualTo(3);
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

    @Test
    void findAll_whenInvalidSize() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(getInvalidRequestMessage(VALID_PAGE, INVALID_SIZE)))
                .build();

        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, INVALID_SIZE,
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

    @Test
    void findAll_whenInvalidOrderBy() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.getInvalidSortingParamRequestMessage(INVALID_ORDER_BY)))
                .build();

        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, INVALID_ORDER_BY)
                )
                .when()
                .get(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllAvailable_whenValidParams() {
        Page<Driver> driverPage = driverRepository.findAll(
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY))
        );
        List<DriverResponse> expected = driverPage.stream()
                .map(driverMapper::mapToDto)
                .toList();
        var actual = given()
                .port(port)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(DEFAULT_PATH + AVAILABLE_PARAM)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("drivers", DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
        assertThat(driverRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void changeStatus_whenDriverNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Driver", "id", 99L)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, 99L)
                .contentType(ContentType.JSON)
                .when()
                .put(CHANGE_STATUS_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void changeStatus_shouldReturnMessageResponse() {
        DriverResponse expected = DriverTestUtils.getDefaultDriverResponse();
        expected.setStatus("UNAVAILABLE");

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, DEFAULT_ID)
                .contentType(ContentType.JSON)
                .when()
                .put(CHANGE_STATUS_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
