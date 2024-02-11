package by.sergo.rideservice.integration.controller;

import by.sergo.rideservice.controller.handler.RestErrorResponse;
import by.sergo.rideservice.controller.handler.ValidationExceptionResponse;
import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
import by.sergo.rideservice.domain.dto.response.RideResponse;
import by.sergo.rideservice.domain.enums.PaymentMethod;
import by.sergo.rideservice.domain.enums.Status;
import by.sergo.rideservice.integration.config.IntegrationTestConfig;
import by.sergo.rideservice.integration.config.WireMockConfig;
import by.sergo.rideservice.mapper.RideMapper;
import by.sergo.rideservice.repository.RideRepository;
import by.sergo.rideservice.util.ExceptionMessageUtil;
import by.sergo.rideservice.util.RideTestUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static by.sergo.rideservice.domain.enums.Status.*;
import static by.sergo.rideservice.integration.ResponseMocks.*;
import static by.sergo.rideservice.util.RideTestUtil.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ContextConfiguration(classes = {WireMockConfig.class})
public class RideIntegrationTest extends IntegrationTestConfig {
    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final WireMockServer mockPassengerService;
    private final WireMockServer mockDriverService;
    private final WireMockServer mockCreditCardService;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() throws IOException {
        setupMockPassengerResponse(mockPassengerService);
        setupMockDriverResponse(mockDriverService);
        setupMockCreditCardResponse(mockCreditCardService);
    }

    @Test
    void createRide_whenDataIsValidAndPaymentByCash() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        request.setPaymentMethod(PaymentMethod.CASH.name());
        RideResponse expected = RideTestUtil.getDefaultRideResponse();

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(RideResponse.class);

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("id", "creatingTime", "price")
                .isEqualTo(actual);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void createRide_whenDataNotValid() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        request.setPaymentMethod(PaymentMethod.CASH.name());
        request.setPickUpAddress(null);

        ValidationExceptionResponse expected = RideTestUtil.getRatingValidationExceptionResponse();

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionResponse.class);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void createRide_whenDataIsValidAndPaymentByCard() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        RideResponse expected = RideTestUtil.getDefaultRideResponse();
        expected.setPaymentMethod("CARD");

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post(DEFAULT_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(RideResponse.class);

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("id", "creatingTime", "price")
                .isEqualTo(actual);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void getRideById_whenRideExists() {
        RideResponse expected = RideTestUtil.getDefaultRideResponse();
        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, DEFAULT_RIDE_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);

        assertEquals(expected, actual);
    }

    @Test
    void getRideById_whenRideNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Ride", "id", NOT_EXIST_ID)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, NOT_EXIST_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertEquals(expected, actual);
    }

    @Test
    void deleteById_whenRideNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Ride", "id", NOT_EXIST_ID)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, NOT_EXIST_ID)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteById_whenRideExists() {
        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, DEFAULT_RIDE_ID)
                .when()
                .delete(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    @Test
    void updateRide_whenDataIsValid() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        request.setPaymentMethod(PaymentMethod.CASH.name());
        Ride ride = rideMapper.mapToEntity(request);
        Ride saved = rideRepository.save(ride);
        String id = saved.getId().toString();
        request.setPickUpAddress("home2");
        RideResponse expected = RideTestUtil.getDefaultRideResponse();
        expected.setId(id);
        expected.setPickUpAddress("home2");

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam(ID_PARAM, id)
                .body(request)
                .when()
                .put(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("creatingTime", "price")
                .isEqualTo(actual);
    }

    @Test
    void rejectRide_whenDataIsValid() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        request.setPaymentMethod(PaymentMethod.CASH.name());
        Ride ride = rideMapper.mapToEntity(request);
        Ride saved = rideRepository.save(ride);
        String id = saved.getId().toString();
        RideResponse expected = RideTestUtil.getDefaultRideResponse();
        expected.setId(id);
        expected.setStatus(Status.REJECTED);

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam(ID_PARAM, id)
                .body(request)
                .when()
                .put(DEFAULT_REJECT_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("creatingTime", "price")
                .isEqualTo(actual);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void rejectRide_whenStatusNotCreated() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        request.setPaymentMethod(PaymentMethod.CASH.name());
        Ride ride = rideMapper.mapToEntity(request);
        Ride saved = rideRepository.save(ride);
        saved.setStatus(Status.ACCEPTED);
        rideRepository.save(saved);
        String id = saved.getId().toString();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.canNotChangeStatusMessage(REJECTED.toString(), saved.getStatus().toString(), CREATED.toString())))
                .build();

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam(ID_PARAM, id)
                .body(request)
                .when()
                .put(DEFAULT_REJECT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void startRide_whenDataIsValid() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        request.setPaymentMethod(PaymentMethod.CASH.name());
        Ride ride = rideMapper.mapToEntity(request);
        Ride saved = rideRepository.save(ride);
        saved.setStatus(Status.ACCEPTED);
        rideRepository.save(saved);
        String id = saved.getId().toString();
        RideResponse expected = RideTestUtil.getDefaultRideResponse();
        expected.setId(id);
        expected.setStatus(Status.TRANSPORT);

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam(ID_PARAM, id)
                .body(request)
                .when()
                .put(DEFAULT_START_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("creatingTime", "price", "startTime")
                .isEqualTo(actual);
    }

    @Test
    void startRide_whenStatusNotAccepted() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        request.setPaymentMethod(PaymentMethod.CASH.name());
        Ride ride = rideMapper.mapToEntity(request);
        Ride saved = rideRepository.save(ride);
        saved.setStatus(FINISHED);
        rideRepository.save(saved);
        String id = saved.getId().toString();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.canNotChangeStatusMessage(TRANSPORT.toString(), saved.getStatus().toString(), ACCEPTED.toString())))
                .build();

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam(ID_PARAM, id)
                .body(request)
                .when()
                .put(DEFAULT_START_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void endRide_whenDataIsValid() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        request.setPaymentMethod(PaymentMethod.CASH.name());
        Ride ride = rideMapper.mapToEntity(request);
        Ride saved = rideRepository.save(ride);
        saved.setStatus(Status.TRANSPORT);
        rideRepository.save(saved);
        String id = saved.getId().toString();
        RideResponse expected = RideTestUtil.getDefaultRideResponse();
        expected.setId(id);
        expected.setStatus(Status.FINISHED);

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam(ID_PARAM, id)
                .body(request)
                .when()
                .put(DEFAULT_FINISH_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RideResponse.class);

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("creatingTime", "price", "startTime", "endTime")
                .isEqualTo(actual);
    }

    @Test
    void endRide_whenStatusNotTransport() {
        RideCreateUpdateRequest request = RideTestUtil.getRideCreateRequest();
        request.setPaymentMethod(PaymentMethod.CASH.name());
        Ride ride = rideMapper.mapToEntity(request);
        Ride saved = rideRepository.save(ride);
        String id = saved.getId().toString();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.canNotChangeStatusMessage(FINISHED.toString(), saved.getStatus().toString(), TRANSPORT.toString())))
                .build();

        var actual = given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam(ID_PARAM, id)
                .body(request)
                .when()
                .put(DEFAULT_FINISH_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void getAllRidesByPassengerId_whenParamsValid() {
        Page<Ride> ridePage = rideRepository.findAllByPassengerIdAndStatus(DEFAULT_PASSENGER_ID, FINISHED,
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY)));
        List<RideResponse> expected = ridePage.stream()
                .map(rideMapper::mapToDto)
                .toList();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, DEFAULT_PASSENGER_ID)
                .params(Map.of(
                        STATUS_PARAM_NAME, FINISHED,
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(DEFAULT_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("rides", RideResponse.class);

        assertThat(actual.get(0).getId()).isEqualTo(expected.get(0).getId());
        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    void getAllRidesByDriverId_whenParamsValid() {
        Page<Ride> ridePage = rideRepository.findAllByDriverIdAndStatus(DEFAULT_ID, FINISHED,
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE, Sort.by(VALID_ORDER_BY)));
        List<RideResponse> expected = ridePage.stream()
                .map(rideMapper::mapToDto)
                .toList();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, DEFAULT_ID)
                .params(Map.of(
                        STATUS_PARAM_NAME, FINISHED,
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("rides", RideResponse.class);

        assertThat(actual.get(0).getId()).isEqualTo(expected.get(0).getId());
        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    void getAllRidesByDriverId_whenParamsNotValid() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.getInvalidRequestMessage(INVALID_PAGE, VALID_SIZE)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM, DEFAULT_ID)
                .params(Map.of(
                        STATUS_PARAM_NAME, FINISHED,
                        PAGE_PARAM_NAME, INVALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE,
                        ORDER_BY_PARAM_NAME, VALID_ORDER_BY)
                )
                .when()
                .get(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
