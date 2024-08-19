package com.notier;

import com.notier.entity.AlarmEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.entity.MemberEntity;
import com.notier.rateService.RedisService;
import com.notier.repository.AlarmRepository;
import com.notier.repository.CurrencyRepository;
import com.notier.repository.MemberRepository;
import java.util.NoSuchElementException;
import java.util.Random;
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

    @Test
    void insertAlarmSettingCode() {

        CurrencyEntity jpy = currencyRepository.findCurrencyEntityByTicker("JPY")
            .orElseThrow(() -> new NoSuchElementException("엔화가 없다고?"));

        CurrencyEntity usd = currencyRepository.findCurrencyEntityByTicker("USD")
            .orElseThrow(() -> new NoSuchElementException("달러가 없다고?"));

        for (long i = 5L; i < 1600L; i++) {
            MemberEntity memberEntity = memberRepository.findById(i)
                .orElseThrow(() -> new NoSuchElementException("없는 고객 ID입니당"));

            Long jpyWishRate = jpy.getExchangeRate() + random.nextInt(41) - 10;
            Long usdWishRate = usd.getExchangeRate() + random.nextInt(41) - 10;

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
