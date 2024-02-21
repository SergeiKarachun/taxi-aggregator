package by.sergo.driverservice.integration.config;

import by.sergo.driverservice.domain.dto.request.DriverForRideResponse;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@TestConfiguration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, Object> testProducerKafkaTemplate() {
        return new KafkaTemplate<>(testProducerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> testProducerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfigs());
    }

    @Bean
    public Map<String, Object> kafkaProducerConfigs() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                JsonSerializer.ADD_TYPE_INFO_HEADERS, false
        );
    }

    @Bean
    public Map<String, Object> kafkaConsumerConfigs() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, "driver-creation-group",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                JsonDeserializer.VALUE_DEFAULT_TYPE, DriverForRideResponse.class
        );
    }

    @Bean
    public ConsumerFactory<String, DriverForRideResponse> testConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfigs());
    }
}

