package by.sergo.rideservice.kafka;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.DriverForRideResponse;
import by.sergo.rideservice.domain.dto.request.EditDriverStatusRequest;
import by.sergo.rideservice.domain.enums.Status;
import by.sergo.rideservice.repository.RideRepository;
import by.sergo.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DriverConsumer {
    private final RideService rideService;
    private final RideRepository rideRepository;
    private final StatusProducer statusProducer;

    @KafkaListener(topics = "${topic.name.driver}", groupId = "${spring.kafka.consumer.group-id.driver}")
    public void consumeMessage(DriverForRideResponse driver) {
        log.info("message consumed {}", driver);
        if (driver.getRideId().equals("free")) {
            setDriver(driver);
        } else {
            rideService.sendEditStatus(driver);
        }

    }

    private void setDriver(DriverForRideResponse driver) {
        List<Ride> rides = rideRepository.findAll().stream()
                .filter(ride -> ride.getDriverId() == null)
                .toList();
        if (!rides.isEmpty()) {
            Ride rideWithoutDriver = rideRepository.findAll().stream()
                    .filter(ride -> ride.getDriverId() == null)
                    .toList()
                    .get(0);
            rideWithoutDriver.setDriverId(driver.getDriverId());
            rideWithoutDriver.setStatus(Status.ACCEPTED);
            rideRepository.save(rideWithoutDriver);
            statusProducer.sendMessage(EditDriverStatusRequest.builder()
                    .driverId(driver.getDriverId())
                    .build());
        }
    }
}
