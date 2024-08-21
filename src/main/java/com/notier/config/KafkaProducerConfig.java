package com.notier.config;

import com.notier.entity.CurrencyEntity;
import com.notier.repository.CurrencyRepository;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
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
     * 환율별 토픽 한번에 생성
     * 참고로 기존에 존재하는 토픽이면 변경사항 반영안됨
     */
    @Bean
    public NewTopics topics() {

        List<CurrencyEntity> currencyEntities = currencyRepository.findAll();

        NewTopic[] topicArray = currencyEntities.stream()
            .map(currencyEntity -> {
                String key = "currency-" + currencyEntity.getTicker();
                return TopicBuilder.name(key)
                    .partitions(3)
                    .replicas(2).build();
            })
            .toArray(NewTopic[]::new);

        return new NewTopics(topicArray);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
