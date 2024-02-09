package by.sergo.paymentservice.integration;

import by.sergo.paymentservice.controller.handler.RestErrorResponse;
import by.sergo.paymentservice.domain.dto.request.AccountCreateUpdateRequest;
import by.sergo.paymentservice.domain.dto.response.AccountResponse;
import by.sergo.paymentservice.domain.entity.Account;
import by.sergo.paymentservice.integration.config.IntegrationTestConfig;
import by.sergo.paymentservice.integration.config.WireMockConfig;
import by.sergo.paymentservice.mapper.AccountMapper;
import by.sergo.paymentservice.repository.AccountRepository;
import by.sergo.paymentservice.util.AccountTestUtil;
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
import java.util.Collections;

import static by.sergo.paymentservice.integration.ResponseMocks.setupMockDriverResponse;
import static by.sergo.paymentservice.integration.ResponseMocks.setupMockSecondDriverResponse;
import static by.sergo.paymentservice.util.AccountTestUtil.*;
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
public class AccountIntegrationTest extends IntegrationTestConfig {

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    @LocalServerPort
    private int port;

    @Autowired
    private WireMockServer mockDriverService;

    @BeforeEach
    void setUp() throws IOException {
        setupMockDriverResponse(mockDriverService);
        setupMockSecondDriverResponse(mockDriverService);
    }

    @Test
    void createAccount_whenDataUnique() {
        AccountCreateUpdateRequest createRequest = getAccountCreateRequest();
        AccountResponse expected = AccountResponse.builder()
                .id(3L)
                .driverId(createRequest.getDriverId())
                .balance(createRequest.getBalance())
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
                .as(AccountResponse.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("accountNumber")
                .isEqualTo(expected);
    }

    @Test
    void createAccount_whenDataNotUnique() {
        AccountCreateUpdateRequest createRequest = getAccountCreateRequest();
        createRequest.setDriverId(2L);
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.getAlreadyExistMessage("Account", "driverId", createRequest.getDriverId().toString())))
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
    void deleteAccount_whenAccountExists() {
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
    void deleteAccount_whenAccountNotExists() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Account", "id", NOT_EXIST_ID)))
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
    void getAccountById_WhenAccountExists() {
        Account account = accountRepository.findById(DEFAULT_ID).get();
        AccountResponse expected = accountMapper.mapToDto(account);

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(DEFAULT_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AccountResponse.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("accountNumber")
                .isEqualTo(expected);
    }

    @Test
    void getAccountById_WhenAccountNotExists() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Account", "id", NOT_EXIST_ID)))
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
    void getAccountByDriverId_WhenAccountExists() {
        Account account = accountRepository.findByDriverId(2L).get();
        AccountResponse expected = accountMapper.mapToDto(account);

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, 2L)
                .when()
                .get(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AccountResponse.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("accountNumber")
                .isEqualTo(expected);
    }

    @Test
    void getAccountByDriverId_WhenAccountNotExists() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Account", "driverId", NOT_EXIST_ID)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_EXIST_ID)
                .when()
                .get(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalBalance_whenAccountNotExist() {
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .messages(Collections.singletonList(ExceptionMessageUtil.getNotFoundMessage("Account", "driverId", DEFAULT_DRIVER_ID)))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_DRIVER_ID)
                .param(WITHDRAWAL_PARAM, WITHDRAWAL_SUM)
                .when()
                .put(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalBalance_whenDriverNotExist() {
        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_EXIST_ID)
                .param(WITHDRAWAL_PARAM, WITHDRAWAL_SUM)
                .when()
                .put(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .extract();
    }

    @Test
    void withdrawalBalance_whenInsufficientFundsOnBalance() {
        Account account = accountRepository.findByDriverId(2L).get();
        RestErrorResponse expected = RestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(Collections.singletonList(ExceptionMessageUtil.getWithdrawalExceptionMessage("Account", "driverId", "2", account.getBalance().toString())))
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, 2L)
                .param(WITHDRAWAL_PARAM, WITHDRAWAL_SUM.add(WITHDRAWAL_SUM))
                .when()
                .put(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void withdrawalBalance_whenDataIsValid() {
        AccountResponse expected = AccountTestUtil.getAccountResponse();
        expected.setBalance(BigDecimal.valueOf(0.00));
        expected.setDriverId(2L);

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, 2L)
                .param(WITHDRAWAL_PARAM, WITHDRAWAL_SUM)
                .when()
                .put(DEFAULT_DRIVER_ID_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AccountResponse.class);

        assertEquals(0, actual.getBalance().compareTo(expected.getBalance()));
    }
}

