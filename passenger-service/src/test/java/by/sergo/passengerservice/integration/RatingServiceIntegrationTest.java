package by.sergo.passengerservice.integration;

import by.sergo.passengerservice.controller.handler.RestErrorResponse;
import by.sergo.passengerservice.controller.handler.ValidationExceptionResponse;
import by.sergo.passengerservice.domain.dto.request.RatingCreateRequest;
import by.sergo.passengerservice.domain.dto.response.DriverResponse;
import by.sergo.passengerservice.domain.dto.response.PassengerRatingResponse;
import by.sergo.passengerservice.domain.dto.response.RatingResponse;
import by.sergo.passengerservice.domain.dto.response.RideResponse;
import by.sergo.passengerservice.integration.config.IntegrationTestConfig;
import by.sergo.passengerservice.integration.config.WireMockConfig;
import by.sergo.passengerservice.mapper.RatingMapper;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.repository.RatingRepository;
import by.sergo.passengerservice.util.PassengerTestUtils;
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
import java.util.Collections;

import static by.sergo.passengerservice.integration.ResponseMocks.setupMockDriverResponse;
import static by.sergo.passengerservice.integration.ResponseMocks.setupMockRideResponse;
import static by.sergo.passengerservice.util.ExceptionMessageUtil.getNotFoundMessage;
import static by.sergo.passengerservice.util.PassengerTestUtils.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/insert-test_values-in-passenger-table.sql",
                "classpath:sql/insert-test-values-in-rating-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/truncate-passenger-table.sql",
                "classpath:sql/truncate-rating-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@ContextConfiguration(classes = { WireMockConfig.class })
public class RatingServiceIntegrationTest extends IntegrationTestConfig {
    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final RatingMapper ratingMapper;
    @LocalServerPort
    private int port;
    @Autowired
    private WireMockServer mockDriverService;
    @Autowired
    private WireMockServer mockRideService;

    @BeforeEach
    void setUp() throws IOException {
        setupMockDriverResponse(mockDriverService);
        setupMockRideResponse(mockRideService);
    }

    @Test
    void ratePassenger_whenDataValid() {
        RatingCreateRequest request = getRatingRequest();
        DriverResponse driverResponse = getDefaultDriverResponse();
        RideResponse rideResponse = getDefaultRideResponse();
        RatingResponse expected = PassengerTestUtils.getRatingResponse();
        expected.setId(4L);
        expected.setDriver(driverResponse);
        expected.setRide(rideResponse);

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .post(RATING_PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(RatingResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addRating_whenDataNotValid() {
        RatingCreateRequest invalidRequest = RatingCreateRequest.builder()
                .grade(25)
                .driverId(0L)
                .build();
        ValidationExceptionResponse expected = getRatingValidationExceptionResponse();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .contentType(ContentType.JSON)
                .body(invalidRequest).when()
                .post(RATING_PATH).then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerRating_whenPassengerNotExist() {
         RestErrorResponse expected = RestErrorResponse.builder()
                 .status(HttpStatus.NOT_FOUND)
                 .messages(Collections.singletonList(getNotFoundMessage("Passenger", "passengerId", NOT_FOUND_ID)))
                 .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, NOT_FOUND_ID)
                .when()
                .get(RATING_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAverageRating_whenPassengerExists() {
        PassengerRatingResponse expected = PassengerRatingResponse.builder()
                .passengerId(DEFAULT_ID)
                .rating(DEFAULT_RATING)
                .build();

        var actual = given()
                .port(port)
                .pathParam(ID_PARAM_NAME, DEFAULT_ID)
                .when()
                .get(RATING_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(PassengerRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
