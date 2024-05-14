package com.notier.rateService;

import com.notier.dto.CurrentCurrencyResponseDto;
import com.notier.dto.SendAlarmResponseDto;
import com.notier.entity.AlarmEntity;
import com.notier.entity.AlarmLogEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.entity.CurrencyLogEntity;
import com.notier.repository.AlarmLogRepository;
import com.notier.repository.AlarmRepository;
import com.notier.repository.CurrencyLogRepository;
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
    private final AlarmLogRepository alarmLogRepository;
    private final CurrencyRepository currencyRepository;
    private final CurrencyLogRepository currencyLogRepository;
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

        // 알람 로그 저장 및 sse 전송
        alarmEntities.stream()
            .map(alarmEntity -> {

                alarmLogRepository.save(
                    AlarmLogEntity.builder()
                        .currencyEntity(alarmEntity.getCurrencyEntity())
                        .memberEntity(alarmEntity.getMemberEntity())
                        .wishRate(alarmEntity.getWishRate())
                        .build()
                );

                if (alarmEntity.getWishRate() > alarmEntity.getCurrencyEntity().getExchangeRate()) {
                    log.info(alarmEntity.getMemberEntity().getName() + "님이 지정하신 지정가보다 낮습니다");
                } else if (alarmEntity.getWishRate().equals(alarmEntity.getCurrencyEntity().getExchangeRate())) {
                    log.info(alarmEntity.getMemberEntity().getName() + "님이 지정하신 지정가입니다");
                } else {
                    log.info(alarmEntity.getMemberEntity().getName() + "님이 지정하신 지정가보다 높습니다");
                }

                return SendAlarmResponseDto.builder()
                    .currencyId(alarmEntity.getCurrencyEntity().getId())
                    .memberId(alarmEntity.getMemberEntity().getId())
                    .memberName(alarmEntity.getMemberEntity().getName())
                    .country(alarmEntity.getCurrencyEntity().getCountry())
                    .exchangeRate(alarmEntity.getCurrencyEntity().getExchangeRate())
                    .build();
            })
            .forEach(sseService::noticeCurrencyToUser);

    }

    /**
     * 환율 변동 확인 메서드
     */
    public CurrentCurrencyResponseDto modifyCurrentCurrency(String country) {
        CurrencyEntity currencyEntity = currencyRepository.findCurrencyEntityByCountry(country)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 국가입니다"));

        Long currencyRate = currencyEntity.getExchangeRate() + random.nextInt(21) - 10;
        modifyCurrencyEntityAndAddLog(currencyEntity, currencyRate);

        return CurrentCurrencyResponseDto.builder()
            .country(country)
            .exchangeRate(currencyRate).build();
    }

    /**
     * 변경된 환율 db update 및 이력 저장
     */
    private void modifyCurrencyEntityAndAddLog(CurrencyEntity currencyEntity, Long currencyRate) {
        currencyEntity.updateExchangeRate(currencyRate);
        currencyRepository.save(currencyEntity);

        CurrencyLogEntity currencyLogEntity = CurrencyLogEntity.builder()
            .country(currencyEntity.getCountry())
            .exchangeRate(currencyRate)
            .build();

        currencyLogRepository.save(currencyLogEntity);
    }

}
