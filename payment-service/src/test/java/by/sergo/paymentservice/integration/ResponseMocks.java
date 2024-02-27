package by.sergo.paymentservice.integration;

import by.sergo.paymentservice.domain.dto.response.UserResponse;
import by.sergo.paymentservice.util.AccountTestUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

public class ResponseMocks {
    public static void setupMockPassengerResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlMatching("/api/v1/passengers/" + AccountTestUtil.DEFAULT_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        UserResponse.class.getClassLoader().getResourceAsStream("payload/get-passenger-response.json"),
                                        defaultCharset()))));
    }
    public static void setupMockSecondPassengerResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlMatching("/api/v1/passengers/" + 2L))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        UserResponse.class.getClassLoader().getResourceAsStream("payload/get-passenger-response.json"),
                                        defaultCharset()))));
    }
    public static void setupMockDriverResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlMatching("/api/v1/drivers/" + AccountTestUtil.DEFAULT_ID))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        UserResponse.class.getClassLoader().getResourceAsStream("payload/get-driver-response.json"),
                                        defaultCharset()))));
    }

    public static void setupMockSecondDriverResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlMatching("/api/v1/drivers/" + 2L))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        UserResponse.class.getClassLoader().getResourceAsStream("payload/get-driver-response.json"),
                                        defaultCharset()))));
    }

    public static void setupMockThirstDriverResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlMatching("/api/v1/drivers/" + 3L))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        UserResponse.class.getClassLoader().getResourceAsStream("payload/get-driver-response.json"),
                                        defaultCharset()))));
    }
}
