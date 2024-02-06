package by.sergo.rideservice.kafka;

import by.sergo.rideservice.domain.dto.request.FindDriverForRideRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideProducer {
    @Value("${topic.name.ride}")
    private String rideTopic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(FindDriverForRideRequest request) {
        log.info(String.format("Message sent %s", request));
        kafkaTemplate.send(rideTopic, request);
    }
}
