package by.sergo.rideservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ProducerConfig {
    @Value("${topic.name.ride}")
    private String rideTopic;
    @Value("${topic.name.status}")
    private String statusTopic;
    @Value("${kafka.partitions.count}")
    private int partitionsCount;
    @Value("${kafka.replicas.count}")
    private int replicasCount;
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> properties = Map.of(
                org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                JsonSerializer.ADD_TYPE_INFO_HEADERS, false
        );
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic sendRideTopic() {
        return TopicBuilder.name(rideTopic)
                .partitions(partitionsCount)
                .replicas(replicasCount)
                .build();
    }

    @Bean
    public NewTopic sendStatusTopic() {
        return TopicBuilder.name(statusTopic)
                .partitions(partitionsCount)
                .replicas(replicasCount)
                .build();
    }

}
