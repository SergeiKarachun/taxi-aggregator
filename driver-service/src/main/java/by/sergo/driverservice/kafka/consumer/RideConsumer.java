package by.sergo.driverservice.kafka.consumer;

import by.sergo.driverservice.domain.dto.request.FindDriverForRideRequest;
import by.sergo.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RideConsumer {
    private final DriverService driverService;

    @KafkaListener(topics = "${topic.name.ride}", groupId = "${spring.kafka.consumer.group-id.ride}")
    public void consumeMessage(FindDriverForRideRequest message) {
        log.info("message consumed {}", message);
        driverService.handleDriverForRide(message);
    }

}
