package com.notier.rateService;

import com.notier.entity.MemberEntity;
import com.notier.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HelloService {

    private final MemberRepository memberRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendKafkaTestMessage() {
        List<MemberEntity> memberList = memberRepository.findAll();
        for (MemberEntity memberEntity : memberList) {
            kafkaTemplate.send("error-messages", memberEntity.getName() + " Hello!!!");
        }
    }
    @KafkaListener(id = "error-listen", topics = "error-messages")
    public void listenKafkaMessage(String message) {
        System.out.println("consumer get message : " + message);
    }

}
