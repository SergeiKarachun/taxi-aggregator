package by.sergo.paymentservice.integration;

import by.sergo.paymentservice.controller.handler.RestErrorResponse;
import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.request.PaymentRequest;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;
import by.sergo.paymentservice.domain.entity.CreditCard;
import by.sergo.paymentservice.integration.config.IntegrationTestConfig;
import by.sergo.paymentservice.integration.config.WireMockConfig;
import by.sergo.paymentservice.mapper.CreditCardMapper;
import by.sergo.paymentservice.repository.CreditCardRepository;
import by.sergo.paymentservice.util.ExceptionMessageUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static by.sergo.paymentservice.domain.enums.UserType.DRIVER;
import static by.sergo.paymentservice.domain.enums.UserType.PASSENGER;
import static by.sergo.paymentservice.integration.ResponseMocks.*;
import static by.sergo.paymentservice.util.CreditCardTestUtil.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/insert-test-values-in-credit-card-table.sql",
                "classpath:sql/insert-test-values-in-account-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/truncate-account-table.sql",
                "classpath:sql/truncate-credit-card-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@ContextConfiguration(classes = {WireMockConfig.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreditCardIntegrationTest extends IntegrationTestConfig {
    private final CreditCardRepository creditCardRepository;
    private final CreditCardMapper creditCardMapper;
    @LocalServerPort
    private int port;

    @Autowired
    private WireMockServer mockDriverService;
    @Autowired
    private WireMockServer mockPassengerService;

    @BeforeEach
    void setUp() throws IOException {
        setupMockDriverResponse(mockDriverService);
        setupMockSecondDriverResponse(mockDriverService);
        setupMockThirstDriverResponse(mockDriverService);
        setupMockPassengerResponse(mockPassengerService);
        setupMockSecondPassengerResponse(mockPassengerService);
    }

    @Test
    void createCreditCard_whenDataValid() {
        CreditCardCreateUpdate createRequest = CreditCardCreateUpdate.builder()
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .userId(2L)
                .userType(PASSENGER.name())
                .build();
        CreditCardResponse expected = CreditCardResponse.builder()
                .id(4L)
                .creditCardNumber(NEW_CREDIT_CARD_NUMBER)
                .expDate(LocalDate.of(2025, 12, 12))
                .cvv(CVV)
                .balance(DEFAULT_BALANCE)
                .user(getUserResponse())
                .userType(PASSENGER.name())
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
                .as(CreditCardResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createCreditCard_whenDataNotUnique() {
        CreditCardCreateUpdate createRequest = getCreditCardRequest();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "userId", createRequest.getUserId(), "userType", createRequest.getUserType())))
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
    void changeCreditCard_whenDataValid() {
        CreditCardCreateUpdate updateRequest = getCreditCardUpdateRequest();
        CreditCardResponse expected = getUpdatedCreditCardResponse();
        expected.setId(2L);

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .pathParam(ID_PARAM_NAME, 2L)
                .body(updateRequest)
                .when()
                .put(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CreditCardResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void changeCreditCard_whenDataNotUnique() {
        CreditCardCreateUpdate updateRequest = getCreditCardRequest();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.getAlreadyExistMessage("Credit Card", "card number", updateRequest.getCreditCardNumber())))
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
    void deleteCreditCard_whenCreditCardExists() {
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
    void deleteCreditCard_whenCreditCardNotExists() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Credit card", "id", NOT_EXIST_ID)))
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
    void getCreditCardByDriverId_WhenCreditCardExists() {
        CreditCard creditCard = creditCardRepository.findByUserIdAndUserType(DEFAULT_ID, DRIVER).get();
        CreditCardResponse expected = creditCardMapper.mapToDto(creditCard);
        expected.setUser(getUserResponse());
        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CreditCardResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCreditCardByDriverId_WhenCreditCardNotExists() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Credit card with type driver", "userId", 3L)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, 3L)
                .when()
                .get(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCreditCardByPassengerId_WhenCreditCardExists() {
        CreditCard creditCard = creditCardRepository.findByUserIdAndUserType(DEFAULT_ID, PASSENGER).get();
        CreditCardResponse expected = creditCardMapper.mapToDto(creditCard);
        expected.setUser(getUserResponse());

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(DEFAULT_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CreditCardResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCreditCardByPassengerId_WhenCreditCardNotExists() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Credit card with type passenger", "userId", 2L)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, 2L)
                .when()
                .get(DEFAULT_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void makePayment_whenDataValid() {
        PaymentRequest request = PaymentRequest.builder()
                .passengerId(DEFAULT_ID)
                .driverId(2L)
                .rideId(DEFAULT_RIDE_ID)
                .sum(DEFAULT_VALUE)
                .build();
        CreditCardResponse expected = getCreditCardAfterPaymentResponse();
        expected.setBalance(BigDecimal.valueOf(90));

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(DEFAULT_PAYMENT_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CreditCardResponse.class);

        assertEquals(0, actual.getBalance().compareTo(expected.getBalance()));
    }

    @Test
    void makePayment_whenCreditCardNotExist() {
        PaymentRequest request = PaymentRequest.builder()
                .passengerId(2L)
                .driverId(2L)
                .rideId(DEFAULT_RIDE_ID)
                .sum(DEFAULT_VALUE)
                .build();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Credit card with type passenger", "userId", 2L)))
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(DEFAULT_PAYMENT_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertEquals(expected, actual);
    }

    @Test
    void makePayment_whenInsufficientFundsOnBalance() {
        PaymentRequest request = PaymentRequest.builder()
                .passengerId(DEFAULT_ID)
                .driverId(2L)
                .rideId(DEFAULT_RIDE_ID)
                .sum(BigDecimal.valueOf(101))
                .build();
        CreditCard creditCard = creditCardRepository.findByUserIdAndUserType(DEFAULT_ID, PASSENGER).get();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.getWithdrawalExceptionMessage("Credit card", "passengerId", request.getPassengerId().toString(), creditCard.getBalance().toString())))
                .build();

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(DEFAULT_PAYMENT_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertEquals(expected, actual);
    }
}
