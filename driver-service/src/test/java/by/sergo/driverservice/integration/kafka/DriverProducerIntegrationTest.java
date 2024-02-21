package by.sergo.driverservice.integration.kafka;

import by.sergo.driverservice.domain.dto.request.DriverForRideResponse;
import by.sergo.driverservice.integration.config.IntegrationTestConfig;
import by.sergo.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.time.Duration;
import java.util.Collections;

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
public class DriverProducerIntegrationTest extends IntegrationTestConfig {
    private final DriverService driverService;

    @Value("${topic.name.driver}")
    private String topic;
    private final ConsumerFactory<String, DriverForRideResponse> testConsumerFactory;

    @Test
    public void sendDriverMessage_WhenStatusChangeToAvailable() {
        driverService.changeStatus(2L);
        Consumer<String, DriverForRideResponse> consumer = testConsumerFactory.createConsumer();
        consumer.subscribe(Collections.singleton(topic));
        ConsumerRecords<String, DriverForRideResponse> records = consumer.poll(Duration.ofMillis(10000L));
        for (ConsumerRecord<String, DriverForRideResponse> record : records) {
            DriverForRideResponse receivedMessage = record.value();
            assertEquals((DriverForRideResponse.builder()
                    .driverId(2L)
                    .rideId("free")
                    .build()), receivedMessage);
        }
        consumer.close();
    }

}
