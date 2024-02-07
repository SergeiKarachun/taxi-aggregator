package by.sergo.driverservice.integration.controller;

import by.sergo.driverservice.domain.dto.response.DriverRatingResponse;
import by.sergo.driverservice.domain.dto.response.PassengerResponse;
import by.sergo.driverservice.domain.dto.response.RatingResponse;
import by.sergo.driverservice.domain.dto.response.RideResponse;
import by.sergo.driverservice.mapper.RatingMapper;
import by.sergo.driverservice.util.RatingTestUtils;
import by.sergo.driverservice.controller.handler.RestErrorResponse;
import by.sergo.driverservice.controller.handler.ValidationExceptionResponse;
import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.integration.config.IntegrationTestConfig;
import by.sergo.driverservice.integration.config.WireMockConfig;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.repository.RatingRepository;
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

import static by.sergo.driverservice.integration.ResponseMocks.setupMockPassengerResponse;
import static by.sergo.driverservice.integration.ResponseMocks.setupMockRideResponse;
import static by.sergo.driverservice.util.ExceptionMessageUtil.getNotFoundMessage;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/insert-test_values-in-driver-table.sql",
                "classpath:sql/insert-test-values-in-rating-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/truncate-driver-table.sql",
                "classpath:sql/truncate-rating-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@ContextConfiguration(classes = { WireMockConfig.class })
public class RatingIntegrationTest extends IntegrationTestConfig {
    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final RatingMapper ratingMapper;
    @LocalServerPort
    private int port;
    @Autowired
    private WireMockServer mockPassengerService;
    @Autowired
    private WireMockServer mockRideService;

    @BeforeEach
    void setUp() throws IOException {
        setupMockPassengerResponse(mockPassengerService);
        setupMockRideResponse(mockRideService);
    }

    @Test
    void ratePassenger_whenDataValid() {
        RatingCreateRequest request = RatingTestUtils.getRatingRequest();
        PassengerResponse passengerResponse = RatingTestUtils.getDefaultPassengerResponse();
        RideResponse rideResponse = RatingTestUtils.getDefaultRideResponse();
        RatingResponse expected = RatingTestUtils.getRatingResponse();
        passengerResponse.setId(RatingTestUtils.DEFAULT_ID);
        expected.setId(4L);
        expected.setPassenger(passengerResponse);
        expected.setRide(rideResponse);

        var actual = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam(RatingTestUtils.ID_PARAM_NAME, RatingTestUtils.DEFAULT_ID)
                .when()
                .post(RatingTestUtils.RATING_PATH)
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
                .passengerId(0L)
                .build();
        ValidationExceptionResponse expected = RatingTestUtils.getRatingValidationExceptionResponse();

        var actual = given()
                .port(port)
                .pathParam(RatingTestUtils.ID_PARAM_NAME, RatingTestUtils.DEFAULT_ID)
                .contentType(ContentType.JSON)
                .body(invalidRequest).when()
                .post(RatingTestUtils.RATING_PATH).then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPassengerRating_whenPassengerNotExist() {
         RestErrorResponse expected = RestErrorResponse.builder()
                 .status(HttpStatus.NOT_FOUND)
                 .messages(Collections.singletonList(getNotFoundMessage("Driver", "id", 99L)))
                 .build();

        var actual = given()
                .port(port)
                .pathParam(RatingTestUtils.ID_PARAM_NAME, 99L)
                .when()
                .get(RatingTestUtils.RATING_PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(RestErrorResponse.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAverageRating_whenPassengerExists() {
        DriverRatingResponse expected = DriverRatingResponse.builder()
                .driverId(RatingTestUtils.DEFAULT_ID)
                .rating(RatingTestUtils.DEFAULT_RATING)
                .build();

        var actual = given()
                .port(port)
                .pathParam(RatingTestUtils.ID_PARAM_NAME, RatingTestUtils.DEFAULT_ID)
                .when()
                .get(RatingTestUtils.RATING_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverRatingResponse.class);

        assertThat(actual).isEqualTo(expected);
    }
}
