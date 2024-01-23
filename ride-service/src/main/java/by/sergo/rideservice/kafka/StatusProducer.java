package by.sergo.rideservice.kafka;

import by.sergo.rideservice.domain.dto.request.EditDriverStatusRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusProducer {
    @Value("${topic.name.status}")
    private String statusTopic;
    private final KafkaTemplate<String, Object> statusKafkaTemplate;

    public void sendMessage(EditDriverStatusRequest request) {
        log.info(String.format("Message sent %s", request));
        statusKafkaTemplate.send(statusTopic, request);
    }
}
