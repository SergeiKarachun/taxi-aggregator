package by.sergo.driverservice.integration;

import by.sergo.driverservice.util.RatingTestUtils;
import by.sergo.driverservice.domain.dto.response.PassengerResponse;
import by.sergo.driverservice.domain.dto.response.RideResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

public class ResponseMocks {
    public static void setupMockPassengerResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlMatching(RatingTestUtils.DEFAULT_PASSENGER_PATH + RatingTestUtils.DEFAULT_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        PassengerResponse.class.getClassLoader().getResourceAsStream("payload/get-passenger-response.json"),
                                        defaultCharset()))));
    }

    public static void setupMockRideResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlMatching(RatingTestUtils.DEFAULT_RIDE_PATH + RatingTestUtils.DEFAULT_RIDE_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        RideResponse.class.getClassLoader().getResourceAsStream("payload/get-ride-response.json"),
                                        defaultCharset()))));
    }
}
