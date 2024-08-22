package com.notier;

import com.notier.entity.AlarmEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.entity.MemberEntity;
import com.notier.rateService.RedisService;
import com.notier.repository.AlarmRepository;
import com.notier.repository.CurrencyRepository;
import com.notier.repository.MemberRepository;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class InsertAlarmTest {

    private final Random random = new Random();
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RedisService redisService;

    /**
     * USD, JPY 알람 엔티티 생성
     * 로직 : 모든 유저 알람 추가
     */
    @Test
    void insertAlarmSettingCode() {

        CurrencyEntity jpy = currencyRepository.findCurrencyEntityByTicker("JPY")
            .orElseThrow(() -> new NoSuchElementException("엔화가 없다고?"));

        CurrencyEntity usd = currencyRepository.findCurrencyEntityByTicker("USD")
            .orElseThrow(() -> new NoSuchElementException("달러가 없다고?"));

        for (long i = 5L; i < 1600L; i++) {
            MemberEntity memberEntity = memberRepository.findById(i)
                .orElseThrow(() -> new NoSuchElementException("없는 고객 ID입니당"));

            Long jpyWishRate = jpy.getExchangeRate() + random.nextInt(41) - 20;
            Long usdWishRate = usd.getExchangeRate() + random.nextInt(41) - 20;

            AlarmEntity jpyAE = AlarmEntity.builder()
                .currencyEntity(jpy)
                .memberEntity(memberEntity)
                .wishRate(jpyWishRate)
                .build();

            AlarmEntity usdAE = AlarmEntity.builder()
                .currencyEntity(usd)
                .memberEntity(memberEntity)
                .wishRate(usdWishRate)
                .build();

            alarmRepository.save(jpyAE);
            alarmRepository.save(usdAE);
        }

    }

    /**
     * USD, JPY 제외 티커들 알람 엔티티 생성
     * 로직 : 각 티커별 랜덤 100명씩만 추가
     */
    @Test
    void insertAlarmInAllTickerExceptUsdJpy() {

        List<Long> numbers = LongStream.range(5, 1600)
            .boxed().collect(Collectors.toList());

        List<CurrencyEntity> currencyEntities = currencyRepository.findAll();

        for (CurrencyEntity currencyEntity : currencyEntities) {
            String ticker = currencyEntity.getTicker();
            if (ticker.equals("USD") || ticker.equals("JPY")) {
                continue;
            }

            Collections.shuffle(numbers, new Random());

            List<Long> randomNumbers = numbers.subList(0, 100);

            for (Long randomNumber : randomNumbers) {
                MemberEntity memberEntity = memberRepository.findById(randomNumber)
                    .orElseThrow(() -> new NoSuchElementException("없는 고객 ID입니당"));

                Long wishRate = currencyEntity.getExchangeRate() + random.nextInt(41) - 20;

                AlarmEntity ae = AlarmEntity.builder()
                    .currencyEntity(currencyEntity)
                    .memberEntity(memberEntity)
                    .wishRate(wishRate)
                    .build();

                alarmRepository.save(ae);
            }

        }

    }

    @Test
    void RedisTest() {
        AlarmEntity alarmEntity = alarmRepository.findAlarmEntitiesByCurrencyTicker("USD").getFirst();
        redisService.setSendAlarm(alarmEntity);
        String key =
            "check_send_alarm:" + alarmEntity.getCurrencyEntity().getTicker() + alarmEntity.getMemberEntity().getId();
        System.out.println("alarm key = " + key);
        boolean checkedAlreadySendAlarm = redisService.checkAlreadySendAlarm(alarmEntity);

        Assertions.assertThat(checkedAlreadySendAlarm).isTrue();
    }


}
