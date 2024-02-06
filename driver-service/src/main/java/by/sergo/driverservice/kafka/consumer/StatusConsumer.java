package by.sergo.driverservice.kafka.consumer;

import by.sergo.driverservice.domain.dto.request.EditDriverStatusRequest;
import by.sergo.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatusConsumer {
    private final DriverService driverService;

    @KafkaListener(topics = "${topic.name.status}", groupId = "${spring.kafka.consumer.group-id.status}", containerFactory = "statusKafkaListenerContainerFactory")
    public void consumeMessage(EditDriverStatusRequest message) {
        log.info("message consumed {}", message);
        driverService.changeStatus(message.getDriverId());
    }


}