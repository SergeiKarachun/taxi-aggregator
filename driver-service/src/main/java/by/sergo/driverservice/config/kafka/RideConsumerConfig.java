package by.sergo.driverservice.config.kafka;

import by.sergo.driverservice.domain.dto.request.FindDriverForRideRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;


import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RideConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, FindDriverForRideRequest> consumerFactory() {
        Map<String, Object> properties = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                JsonDeserializer.VALUE_DEFAULT_TYPE, FindDriverForRideRequest.class
        );
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FindDriverForRideRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FindDriverForRideRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
