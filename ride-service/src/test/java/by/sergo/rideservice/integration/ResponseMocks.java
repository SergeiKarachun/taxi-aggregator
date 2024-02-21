package by.sergo.rideservice.integration;

import by.sergo.rideservice.domain.dto.response.CreditCardResponse;
import by.sergo.rideservice.domain.dto.response.DriverResponse;
import by.sergo.rideservice.domain.dto.response.PassengerResponse;
import by.sergo.rideservice.util.RideTestUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

public class ResponseMocks {
    public static void setupMockPassengerResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo(RideTestUtil.DEFAULT_PASSENGER_PATH + RideTestUtil.DEFAULT_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        PassengerResponse.class.getClassLoader().getResourceAsStream("payload/get-passenger-response.json"),
                                        defaultCharset()))));
    }
    public static void setupMockDriverResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo(RideTestUtil.DEFAULT_DRIVER_PATH + RideTestUtil.DEFAULT_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        DriverResponse.class.getClassLoader().getResourceAsStream("payload/get-driver-response.json"),
                                        defaultCharset()))));
    }

    public static void setupMockCreditCardResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo(RideTestUtil.DEFAULT_CREDIT_CARD_PATH + RideTestUtil.DEFAULT_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        CreditCardResponse.class.getClassLoader().getResourceAsStream("payload/get-credit-card-response.json"),
                                        defaultCharset()))));
    }

}
