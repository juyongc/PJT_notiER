package com.notier.rateService;

import com.notier.dto.CurrentCurrencyResponseDto;
import com.notier.dto.SendAlarmResponseDto;
import com.notier.entity.AlarmEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.repository.AlarmRepository;
import com.notier.repository.CurrencyRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
    private final SseService sseService;
    private final Random random = new Random();

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

    public CurrentCurrencyResponseDto findCurrentCurrency(String country) {
        CurrencyEntity currencyEntity = currencyRepository.findCurrencyEntityByCountry(country)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 국가입니다"));

        Long currencyRate = currencyEntity.getExchangeRate() + random.nextInt(21) - 10;

        return CurrentCurrencyResponseDto.builder()
            .country(country)
            .exchangeRate(currencyRate).build();

    }

}
