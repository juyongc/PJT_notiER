package com.notier.rateService;

import com.notier.dto.SendAlarmResponseDto;
import com.notier.entity.AlarmEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.repository.AlarmRepository;
import com.notier.repository.CurrencyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class RateService {


    private final AlarmRepository alarmRepository;
    private final CurrencyRepository currencyRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SseService sseService;

    public void sendCurrencyMessage() {

        List<CurrencyEntity> currencyEntities = currencyRepository.findAll();
        for (CurrencyEntity currencyEntity : currencyEntities) {
            kafkaTemplate.send("currency-" + currencyEntity.getCountry(), currencyEntity.getCountry(), String.valueOf(currencyEntity.getExchangeRate()));
        }

    }

    /**
     * key가 안받아지고 data만 받아짐
     * key가 country라서 안되는 상황
     * => ConsumerRecord 를 사용해서 key, value를 가져올 수 있다
     */
    @KafkaListener(id = "currency-us-listen", topics = "currency-us")
    public void listenCurrencyAlarm(ConsumerRecord<String,String> consumerRecord) {

        String country = consumerRecord.key();

        List<AlarmEntity> alarmEntities = alarmRepository.findAlarmEntitiesByCurrencyCountry(country);

        alarmEntities.forEach(alarmEntity -> log.info(alarmEntity.toString()));

        alarmEntities.stream()
            .map(alarmEntity -> SendAlarmResponseDto.builder()
                .currencyId(alarmEntity.getCurrencyEntity().getId())
                .memberId(alarmEntity.getMemberEntity().getId())
                .memberName(alarmEntity.getMemberEntity().getName())
                .country(alarmEntity.getCurrencyEntity().getCountry())
                .exchangeRate(alarmEntity.getCurrencyEntity().getExchangeRate())
                .build())
            .forEach(sseService::noticeCurrencyToUser);

    }

}
