package com.notier.controller;

import com.notier.rateService.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HelloController {

    private final HelloService helloService;

    @GetMapping("/hello")
    public String hello() {
        helloService.sendKafkaTestMessage();
        return "hello";
    }


}
