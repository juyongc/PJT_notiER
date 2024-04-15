package com.notier.controller;

import com.notier.rateService.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HelloController {

    private final RateService rateService;

    @GetMapping("/hello")
    public String hello() {
        rateService.sendKafkaMessage();
        return "hello";
    }
}
