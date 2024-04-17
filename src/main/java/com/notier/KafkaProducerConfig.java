package com.notier;

import com.notier.repository.CurrencyRepository;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@RequiredArgsConstructor
@Configuration
public class KafkaProducerConfig {

    private final CurrencyRepository currencyRepository;

    @Bean
    public ProducerFactory<String, String> producerFactory() {

        HashMap<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:10000");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * 새 토픽 생성
     * 왜 repliaction factor가 1일까?
     */
    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("currency-us")
            .partitions(3)
            .replicas(2)
            .build();
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
