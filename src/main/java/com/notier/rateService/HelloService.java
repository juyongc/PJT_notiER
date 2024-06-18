package com.notier.rateService;

import com.notier.entity.MemberEntity;
import com.notier.repository.MemberRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HelloService {

    private final MemberRepository memberRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Random random = new Random();

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

    public String greetingRandomMember() {
        MemberEntity memberEntity = memberRepository.findById(random.nextLong(5, 1000))
            .orElseThrow(() -> new NoSuchElementException("없는 유저네요~~!"));
        return memberEntity.getName();
    }

}
