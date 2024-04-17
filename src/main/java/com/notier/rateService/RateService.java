package com.notier.rateService;

import com.notier.entity.AlarmEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.repository.AlarmRepository;
import com.notier.repository.CurrencyRepository;
import com.notier.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RateService {


    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;
    private final CurrencyRepository currencyRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

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
        String rate = consumerRecord.value();

        List<AlarmEntity> alarmEntities = alarmRepository.findAlarmEntitiesByCurrencyCountry(country);
        for (AlarmEntity alarmEntity : alarmEntities) {

            System.out.println("--------------------------------------------------------");
            System.out.println("User = " + alarmEntity.getMemberEntity().getName());
            System.out.println("Current Currency = " + rate);
            System.out.println("alarmEntity.getWishRate() = " + alarmEntity.getWishRate());
            System.out.println("--------------------------------------------------------");

        }

    }


}
