package by.sergo.rideservice.kafka;

import by.sergo.rideservice.domain.dto.request.DriverForRideResponse;
import by.sergo.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class DriverConsumer {
    private final RideService rideService;

    @KafkaListener(topics = "${topic.name.driver}", groupId = "${spring.kafka.consumer.group-id.driver}")
    public void consumeMessage(DriverForRideResponse driver) {
        log.info("message consumed {}", driver);
        if (driver.getRideId().equals("free")) {
            rideService.setDriver(driver);
        } else {
            rideService.sendEditStatus(driver);
        }

    }
}
