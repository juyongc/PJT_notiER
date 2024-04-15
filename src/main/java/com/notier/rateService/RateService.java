package com.notier.rateService;

import com.notier.entity.MemberEntity;
import com.notier.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RateService {

    private final MemberRepository memberRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendKafkaMessage() {
        List<MemberEntity> memberList = memberRepository.findAll();
        for (MemberEntity memberEntity : memberList) {
            kafkaTemplate.send("error-messages", memberEntity.getName() + " Hello!!!");
        }
    }

    public void sendRateUpDownAlarm() {
    }

}
