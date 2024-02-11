package by.sergo.rideservice.integration.kafka;

import by.sergo.rideservice.domain.dto.request.EditDriverStatusRequest;
import by.sergo.rideservice.integration.config.IntegrationTestConfig;
import by.sergo.rideservice.integration.config.WireMockConfig;
import by.sergo.rideservice.service.RideService;
import by.sergo.rideservice.util.RideTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

import static by.sergo.rideservice.integration.ResponseMocks.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ContextConfiguration(classes = {WireMockConfig.class})
public class StatusProducerTest extends IntegrationTestConfig {
    @Value("${topic.name.status}")
    private String topic;
    private final RideService rideService;
    private final ConsumerFactory<String, Object> testStatusConsumerFactory;
    private final WireMockServer mockPassengerService;
    private final WireMockServer mockDriverService;
    private final WireMockServer mockCreditCardService;

    @BeforeEach
    void setUp() throws IOException {
        setupMockPassengerResponse(mockPassengerService);
        setupMockDriverResponse(mockDriverService);
        setupMockCreditCardResponse(mockCreditCardService);
    }

    @Test
    public void sendStatusMessage_WhenStatusChangedToFinished() {
        rideService.endRide("65c1112fa3c5564557a6e111");
        Consumer<String, Object> consumer = testStatusConsumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton(topic));
        ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(10000L));
        for (ConsumerRecord<String, Object> record : records) {
            ObjectMapper objectMapper = new ObjectMapper();
            EditDriverStatusRequest request = objectMapper.convertValue(record.value(), EditDriverStatusRequest.class);
            assertEquals(EditDriverStatusRequest.builder()
                    .driverId(RideTestUtil.DEFAULT_ID)
                    .build(), request);
        }
        consumer.close();
    }
}
