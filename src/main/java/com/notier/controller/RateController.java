package com.notier.controller;

import com.notier.rateService.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RateController {

    private final RateService rateService;

    @GetMapping("/notice-us")
    public String alarmUsChange() {
        rateService.sendCurrencyMessage();
        return "us dollar~~";
    }
}