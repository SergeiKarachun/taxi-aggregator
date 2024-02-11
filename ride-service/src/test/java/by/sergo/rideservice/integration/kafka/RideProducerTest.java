package by.sergo.rideservice.integration.kafka;

import by.sergo.rideservice.domain.dto.request.FindDriverForRideRequest;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
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

import static by.sergo.rideservice.integration.ResponseMocks.setupMockPassengerResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ContextConfiguration(classes = {WireMockConfig.class})
public class RideProducerTest extends IntegrationTestConfig {
    @Value("${topic.name.ride}")
    private String topic;
    private final RideService rideService;
    private final ConsumerFactory<String, Object> testRideConsumerFactory;
    private final WireMockServer mockPassengerService;

    @BeforeEach
    void setUp() throws IOException {
        setupMockPassengerResponse(mockPassengerService);
    }
    @Test
    public void sendRideMessage_WhenRideCreated() {
        RideCreateUpdateRequest requestForRide = RideTestUtil.getRideCreateRequest();
        requestForRide.setPaymentMethod("CASH");
        var response = rideService.create(requestForRide);
        Consumer<String, Object> consumer = testRideConsumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton(topic));
        ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(10000L));
        for (ConsumerRecord<String, Object> record : records) {
            ObjectMapper objectMapper = new ObjectMapper();
            FindDriverForRideRequest request = objectMapper.convertValue(record.value(), FindDriverForRideRequest.class);
            assertEquals(FindDriverForRideRequest.builder()
                    .rideId(response.getId())
                    .build(), request);
        }
        consumer.close();
    }
}
