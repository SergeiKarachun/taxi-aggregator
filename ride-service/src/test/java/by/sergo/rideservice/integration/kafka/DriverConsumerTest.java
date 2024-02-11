package by.sergo.rideservice.integration.kafka;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.DriverForRideResponse;
import by.sergo.rideservice.integration.config.IntegrationTestConfig;
import by.sergo.rideservice.integration.config.WireMockConfig;
import by.sergo.rideservice.repository.RideRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.time.Duration;

import static by.sergo.rideservice.integration.ResponseMocks.*;
import static by.sergo.rideservice.util.RideTestUtil.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ContextConfiguration(classes = {WireMockConfig.class})
public class DriverConsumerTest extends IntegrationTestConfig {
    @Value("${topic.name.driver}")
    private String topic;

    private final KafkaTemplate<String, DriverForRideResponse> testProducerKafkaTemplate;
    private final RideRepository rideRepository;
    private final WireMockServer mockDriverService;

    @BeforeEach
    void setUp() throws IOException {
        setupMockDriverResponse(mockDriverService);
    }

    @Test
    void editRideInfo_whenDriverMessageConsumed() {
        DriverForRideResponse driverMessage = DriverForRideResponse.builder()
                .driverId(DEFAULT_ID)
                .rideId(DEFAULT_RIDE_ID)
                .build();
        ProducerRecord<String, DriverForRideResponse> record = new ProducerRecord<>(
                topic, driverMessage
        );
        testProducerKafkaTemplate.send(record);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(30, SECONDS)
                .untilAsserted(() -> {
                    Ride ride = rideRepository.findById(DEFAULT_RIDE_ID).get();
                    assertEquals(ride.getDriverId(), driverMessage.getDriverId());
                });
    }
}
