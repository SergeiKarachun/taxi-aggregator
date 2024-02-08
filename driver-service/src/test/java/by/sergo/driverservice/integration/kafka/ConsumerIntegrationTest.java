package by.sergo.driverservice.integration.kafka;

import by.sergo.driverservice.domain.dto.request.EditDriverStatusRequest;
import by.sergo.driverservice.domain.entity.Driver;
import by.sergo.driverservice.domain.enums.Status;
import by.sergo.driverservice.integration.config.IntegrationTestConfig;
import by.sergo.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.time.Duration;

import static by.sergo.driverservice.util.DriverTestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/insert-test_values-in-driver-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/truncate-driver-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConsumerIntegrationTest extends IntegrationTestConfig {
    @Value("${topic.name.status}")
    private String topic;
    private final DriverRepository driverRepository;
    private final KafkaTemplate<String, Object> testProducerKafkaTemplate;

    @Test
    void editDriverStatus_whenStatusMessageConsumed() {
        EditDriverStatusRequest statusRequest = EditDriverStatusRequest.builder()
                .driverId(DEFAULT_ID)
                .build();
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                topic, statusRequest
        );
        testProducerKafkaTemplate.send(record);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(30, SECONDS)
                .untilAsserted(() -> {
                    Driver driver = driverRepository.findById(DEFAULT_ID).get();
                    assertEquals(driver.getStatus(), Status.UNAVAILABLE);
                });
    }
}
