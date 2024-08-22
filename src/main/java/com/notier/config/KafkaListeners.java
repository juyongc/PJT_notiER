package com.notier.config;

import com.notier.rateService.RateService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaListeners {

    private final RateService rateService;

    @KafkaListener(id = "currency-listen-1", topicPattern = "currency-.*", groupId = "currency-group")
    public void listenCurrencyAlarm1(ConsumerRecord<String, String> consumerRecord) {
        rateService.listenCurrencyAlarm(consumerRecord);
    }

    @KafkaListener(id = "currency-listen-2", topicPattern = "currency-.*", groupId = "currency-group")
    public void listenCurrencyAlarm2(ConsumerRecord<String, String> consumerRecord) {
        rateService.listenCurrencyAlarm(consumerRecord);
    }

    @KafkaListener(id = "currency-listen-3", topicPattern = "currency-.*", groupId = "currency-group")
    public void listenCurrencyAlarm3(ConsumerRecord<String, String> consumerRecord) {
        rateService.listenCurrencyAlarm(consumerRecord);
    }

    @KafkaListener(id = "currency-listen-4", topicPattern = "currency-.*", groupId = "currency-group")
    public void listenCurrencyAlarm4(ConsumerRecord<String, String> consumerRecord) {
        rateService.listenCurrencyAlarm(consumerRecord);
    }

}
