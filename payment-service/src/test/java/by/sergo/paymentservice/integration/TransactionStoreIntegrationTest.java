package by.sergo.paymentservice.integration;

import by.sergo.paymentservice.controller.handler.RestErrorResponse;
import by.sergo.paymentservice.domain.dto.response.TransactionStoreResponse;
import by.sergo.paymentservice.domain.entity.TransactionStore;
import by.sergo.paymentservice.integration.config.IntegrationTestConfig;
import by.sergo.paymentservice.integration.config.WireMockConfig;
import by.sergo.paymentservice.mapper.TransactionStoreMapper;
import by.sergo.paymentservice.repository.TransactionStoreRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static by.sergo.paymentservice.util.ExceptionMessageUtil.getInvalidRequestMessage;
import static by.sergo.paymentservice.util.ExceptionMessageUtil.getNotFoundMessage;
import static by.sergo.paymentservice.util.TransactionStoreTestUtil.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/insert-test-values-in-transaction-store-table.sql",
                "classpath:sql/insert-test-values-in-credit-card-table.sql",
                "classpath:sql/insert-test-values-in-account-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/truncate-transaction-store-table.sql",
                "classpath:sql/truncate-account-table.sql",
                "classpath:sql/truncate-credit-card-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@ContextConfiguration(classes = {WireMockConfig.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionStoreIntegrationTest extends IntegrationTestConfig {

    private final TransactionStoreMapper transactionStoreMapper;
    private final TransactionStoreRepository transactionStoreRepository;
    @LocalServerPort
    private int port;

    @Test
    void getDriverTransactionByDriverId_WhenDriverAccountExist() {
        Page<TransactionStore> driverPage = transactionStoreRepository.findAllByAccountNumber(DEFAULT_ACCOUNT_NUMBER,
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE_TEN)
        );
        List<TransactionStoreResponse> expected = driverPage.stream()
                .map(transactionStoreMapper::mapToDto)
                .toList();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, 2L)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE_TEN)
                )
                .when()
                .get(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("transactions", TransactionStoreResponse.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("operationDate", "value")
                .isEqualTo(expected);
        assertThat(actual.size()).isEqualTo(2);
        assertEquals(0, actual.get(0).getValue().compareTo(expected.get(0).getValue()));
        assertEquals(0, actual.get(1).getValue().compareTo(expected.get(1).getValue()));
    }

    @Test
    void getDriverTransactionByDriverId_WhenDriverAccountNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Account", "driverId", DEFAULT_ID)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE_TEN)
                )
                .when()
                .get(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getDriverTransactionByDriverId_WhenInvalidPageAndSize() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(getInvalidRequestMessage(INVALID_PAGE, INVALID_SIZE)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, 2L)
                .params(Map.of(
                        PAGE_PARAM_NAME, INVALID_PAGE,
                        SIZE_PARAM_NAME, INVALID_SIZE)
                )
                .when()
                .get(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerTransactionByPassengerId_WhenPassengerCreditCardExist() {
        Page<TransactionStore> driverPage = transactionStoreRepository.findAllByCreditCardNumber(DEFAULT_CREDIT_CARD_NUMBER,
                PageRequest.of(VALID_PAGE - 1, VALID_SIZE_TEN)
        );
        List<TransactionStoreResponse> expected = driverPage.stream()
                .map(transactionStoreMapper::mapToDto)
                .toList();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE_TEN)
                )
                .when()
                .get(DEFAULT_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath().getList("transactions", TransactionStoreResponse.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("operationDate", "value")
                .isEqualTo(expected);
        assertThat(actual.size()).isEqualTo(1);
        assertEquals(0, actual.get(0).getValue().compareTo(expected.get(0).getValue()));
    }

    @Test
    void getPassengerTransactionByPassengerId_WhenPassengerCreditCardNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(getNotFoundMessage("Credit card", "passengerId", NOT_EXIST)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_EXIST)
                .params(Map.of(
                        PAGE_PARAM_NAME, VALID_PAGE,
                        SIZE_PARAM_NAME, VALID_SIZE_TEN)
                )
                .when()
                .get(DEFAULT_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerTransactionByPassengerId_WhenInvalidPageAndSize() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(getInvalidRequestMessage(INVALID_PAGE, INVALID_SIZE)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .params(Map.of(
                        PAGE_PARAM_NAME, INVALID_PAGE,
                        SIZE_PARAM_NAME, INVALID_SIZE)
                )
                .when()
                .get(DEFAULT_PASSENGER_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
