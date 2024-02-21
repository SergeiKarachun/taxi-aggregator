package by.sergo.passengerservice.integration;

import by.sergo.passengerservice.domain.dto.response.DriverResponse;
import by.sergo.passengerservice.domain.dto.response.RideResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static by.sergo.passengerservice.util.PassengerTestUtils.*;
import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

public class ResponseMocks {
    public static void setupMockDriverResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlMatching( DEFAULT_DRIVER_PATH + DEFAULT_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        DriverResponse.class.getClassLoader().getResourceAsStream("payload/get-driver-response.json"),
                                        defaultCharset()))));
    }

    public static void setupMockRideResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlMatching(DEFAULT_RIDE_PATH + DEFAULT_RIDE_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        RideResponse.class.getClassLoader().getResourceAsStream("payload/get-ride-response.json"),
                                        defaultCharset()))));
    }
}
