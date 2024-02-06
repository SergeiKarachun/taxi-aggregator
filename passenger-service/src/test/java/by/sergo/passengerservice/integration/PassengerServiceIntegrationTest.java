package by.sergo.passengerservice.integration;

import by.sergo.passengerservice.controller.handler.RestErrorResponse;
import by.sergo.passengerservice.domain.dto.request.PassengerCreateUpdateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerResponse;
import by.sergo.passengerservice.domain.entity.Passenger;
import by.sergo.passengerservice.mapper.PassengerMapper;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.util.ExceptionMessageUtil;
import by.sergo.passengerservice.util.PassengerTestUtils;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Collections;
import java.util.HashMap;

import static by.sergo.passengerservice.util.ExceptionMessageUtil.*;
import static by.sergo.passengerservice.util.PassengerTestUtils.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/insert-test_values-in-passenger-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/truncate-passenger-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PassengerServiceIntegrationTest extends IntegrationTestConfig{
    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    @LocalServerPort
    private int port;

    @Test
    void addPassenger_whenDataIsValidAndUnique() {
        PassengerCreateUpdateRequest createRequest = PassengerTestUtils.getUniquePassengerRequest();

        PassengerResponse expected = PassengerResponse.builder()
                .id(NEW_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .rating(DEFAULT_RATING)
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
                .as(PassengerResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addPassenger_whenDataNotUnique() {
        PassengerCreateUpdateRequest createRequest = getPassengerRequest();
        HashMap<String, String> errors = new HashMap<>();
        errors.put("email", getAlreadyExistMessage("Passenger", "email", createRequest.getEmail()));
        errors.put("phone", getAlreadyExistMessage("Passenger", "phone", createRequest.getPhone()));

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
    void updateById_whenPassengerNotExist() {
        PassengerCreateUpdateRequest updateRequest = getUniquePassengerRequest();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Passenger", "id", "99")))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
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
        PassengerCreateUpdateRequest updateRequest = getUniquePassengerRequest();
        PassengerResponse expected = PassengerResponse.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .email(UNIQUE_EMAIL)
                .phone(UNIQUE_PHONE)
                .rating(DEFAULT_RATING)
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
                .as(PassengerResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateById_WhenDataNotUnique() {
        PassengerCreateUpdateRequest updateRequest = getPassengerRequest();
        HashMap<String, String> errors = new HashMap<>();
        errors.put("email", getAlreadyExistMessage("Passenger", "email", updateRequest.getEmail()));
        errors.put("phone", getAlreadyExistMessage("Passenger", "phone", updateRequest.getPhone()));

        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(getAlreadyExistMapMessage(errors)))
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAM_NAME, 2L)
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
    void deleteById_whenPassengerNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Passenger", "id", "99")))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById_whenPassengerExists() {
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
    void findById_WhenPassengerNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Passenger", "id", "99")))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findById_whenPassengerExists() {
        Passenger passenger = passengerRepository.findById(DEFAULT_ID).get();
        PassengerResponse expected = passengerMapper.mapToDto(passenger);

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerResponse.class);

        assertThat(actual).isEqualTo(expected);
    }


}
