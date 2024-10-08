package com.notier.controller;

import com.notier.rateService.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kafka, Redis 등 tool healthCheck용 컨트롤러
 */
@RequiredArgsConstructor
@RestController
public class HelloController {

    private final HelloService helloService;

    @GetMapping("/hello")
    public String hello() {
        helloService.sendKafkaTestMessage();
        return "hello";
    }

    @GetMapping("greeting")
    public String greeting() {
        String memberName = helloService.greetingRandomMember();
        return "Greeting!!! " + memberName;
    }

}
