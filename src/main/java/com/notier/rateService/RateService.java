package com.notier.rateService;

import com.notier.backOffice.ExchangeResponseDto;
import com.notier.dto.CurrentCurrencyResponseDto;
import com.notier.dto.SendAlarmResponseDto;
import com.notier.entity.AlarmEntity;
import com.notier.entity.AlarmLogEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.entity.CurrencyLogEntity;
import com.notier.entity.MemberEntity;
import com.notier.repository.AlarmLogRepository;
import com.notier.repository.AlarmRepository;
import com.notier.repository.CurrencyLogRepository;
import com.notier.repository.CurrencyRepository;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final RedisService redisService;
    private final SseService sseService;
    private final RestTemplate restTemplate;
    private final Random random = new Random();

    @Value("${api.exchange-mock-currency-url}")
    private String baseUrl;

    public void sendCurrencyMessage(CurrencyEntity currencyEntity) {
        kafkaTemplate.send("currency-" + currencyEntity.getTicker(), currencyEntity.getTicker(),
            String.valueOf(currencyEntity.getExchangeRate()));
    }

    /**
     * 환율 변경된 정보 받으면 카프카 리스너가 행동할 메서드
     * - 알람 전송
     */
    public void listenCurrencyAlarm(ConsumerRecord<String,String> consumerRecord) {

        String ticker = consumerRecord.key();

        List<AlarmEntity> alarmEntities = alarmRepository.findAlarmEntitiesByCurrencyTicker(ticker);

        // 알람 로그 저장 및 sse 전송
        alarmEntities.stream()
            .map(alarmEntity -> {

                // 해당 통화로 유저에게 오늘 알람 보낸적 있는지 확인 -> 있으면 건너뜀
                boolean checkedSendAlarm = redisService.checkAlreadySendAlarm(alarmEntity);
                if (checkedSendAlarm) {
                    return null;
                }

                Long userWishRate = alarmEntity.getWishRate();
                CurrencyEntity currencyEntity = alarmEntity.getCurrencyEntity();
                MemberEntity memberEntity = alarmEntity.getMemberEntity();

                alarmLogRepository.save(
                    AlarmLogEntity.builder()
                        .currencyEntity(currencyEntity)
                        .memberEntity(memberEntity)
                        .wishRate(userWishRate)
                        .build()
                );

                /**
                 * TODO
                 * 쿼리 최적화할 수 있을듯
                 * 매번 검색하지 말고 previous 값을 미리 가지고 있다면 1번만 select 해오면 됨
                 **/
                Long previousExchangeRate = getPreviousExchangeRate(currencyEntity);
                Long currentExchangeRate = currencyEntity.getExchangeRate();

                //  쿼리 생각해볼것
                if (previousExchangeRate >= userWishRate && userWishRate > currentExchangeRate) {
                    redisService.setSendAlarm(alarmEntity);
                    log.info(memberEntity.getName() + "님이 지정하신 지정가보다 낮습니다");
                    log.info("\n" + "현재가 = {}, 지정가 = {}, 과거가 = {}", currentExchangeRate, userWishRate,
                        previousExchangeRate);
                } else if (previousExchangeRate <= userWishRate && userWishRate < currentExchangeRate) {
                    redisService.setSendAlarm(alarmEntity);
                    log.info(memberEntity.getName() + "님이 지정하신 지정가보다 높습니다");
                    log.info("\n" + "현재가 = {}, 지정가 = {}, 과거가 = {}", currentExchangeRate, userWishRate,
                        previousExchangeRate);
                } else if (userWishRate.equals(currentExchangeRate)) {
                    redisService.setSendAlarm(alarmEntity);
                    log.info(memberEntity.getName() + "님이 지정하신 지정가입니다");
                    log.info("\n" + "현재가 = {}, 지정가 = {}, 과거가 = {}", currentExchangeRate, userWishRate,
                        previousExchangeRate);
                } else {
                    return null;
                }

                return SendAlarmResponseDto.builder()
                    .currencyId(currencyEntity.getId())
                    .memberId(memberEntity.getId())
                    .memberName(memberEntity.getName())
                    .ticker(currencyEntity.getTicker())
                    .exchangeRate(currentExchangeRate)
                    .build();
            })
            .forEach(sseService::noticeCurrencyToUser);
    }

    private Long getPreviousExchangeRate(CurrencyEntity currencyEntity) {
        Long previousExchangeRate = currencyLogRepository.previousExchangeRate(currencyEntity.getTicker());
        return previousExchangeRate != null ? previousExchangeRate : 0L;
    }

    /**
     * 외부 api 콜을 통한 currency Entity 갱신 및 카프카 메시지 전송
     */
    public List<ExchangeResponseDto> callInternalCurrencyApi() {

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .build()
            .toUri();
        ResponseEntity<List<ExchangeResponseDto>> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<ExchangeResponseDto>>() {
            });

        List<ExchangeResponseDto> responseDtoList = Optional.ofNullable(responseEntity.getBody())
            .orElseThrow(() -> new IllegalArgumentException("Need to check! We get null!!"));

        responseDtoList.stream()
            .filter(res -> !res.getTicker().equals("KRW"))
            .forEach(res -> {
                CurrencyEntity currencyEntity = getCurrencyEntity(res);
                modifyCurrencyEntityAndAddLog(currencyEntity, res.getExchangeRate());
                sendCurrencyMessage(currencyEntity);
            });

        return responseDtoList;
    }

    /**
     * CurrencyEntity에서 해당 통화를 찾거나 없으면 신규 생성
     */
    private CurrencyEntity getCurrencyEntity(ExchangeResponseDto responseDto) {

        return currencyRepository.findCurrencyEntityByTicker(responseDto.getTicker())
            .orElseGet(() -> CurrencyEntity.builder()
                .ticker(responseDto.getTicker())
                .explanation(responseDto.getExplanation())
                .exchangeRate(responseDto.getExchangeRate())
                .build());
    }

    /**
     * 환율 변동 메서드
     */
    public CurrentCurrencyResponseDto modifyCurrentCurrency(String ticker) {
        CurrencyEntity currencyEntity = currencyRepository.findCurrencyEntityByTicker(ticker)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 국가입니다"));

        Long currencyRate = currencyEntity.getExchangeRate() + random.nextInt(21) - 10;
        modifyCurrencyEntityAndAddLog(currencyEntity, currencyRate);
        sendCurrencyMessage(currencyEntity);

        return CurrentCurrencyResponseDto.builder()
            .ticker(ticker)
            .exchangeRate(currencyRate).build();
    }

    /**
     * 변경된 환율 수정 후 db update & 이력 저장
     */
    private void modifyCurrencyEntityAndAddLog(CurrencyEntity currencyEntity, Long exchangeRate) {

        currencyEntity.updateExchangeRate(exchangeRate);
        currencyRepository.save(currencyEntity);

        CurrencyLogEntity currencyLogEntity = CurrencyLogEntity.builder()
            .ticker(currencyEntity.getTicker())
            .explanation(currencyEntity.getExplanation())
            .exchangeRate(currencyEntity.getExchangeRate())
            .build();
        currencyLogRepository.save(currencyLogEntity);
    }

}
