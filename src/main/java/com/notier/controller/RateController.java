package com.notier.controller;

import com.notier.dto.CurrentCurrencyResponseDto;
import com.notier.rateService.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * 실시간 환율 가져오는 api는 현재 유료 서비스라서 임의로 실시간 환율 주는 api
     * db에 저장된 기준값을 기준으로 -10 ~ +10 까지 랜덤으로 값을 줌
     */
    @GetMapping("/current-currency/{country}")
    public ResponseEntity<CurrentCurrencyResponseDto> getCurrentCurrency(@PathVariable("country") String country) {

        CurrentCurrencyResponseDto responseDto = rateService.findCurrentCurrency(country);

        return ResponseEntity.ok(responseDto);
    }
}