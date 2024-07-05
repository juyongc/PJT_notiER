package com.notier.controller;

import com.notier.backOffice.ExchangeResponseDto;
import com.notier.dto.CurrentCurrencyResponseDto;
import com.notier.entity.CurrencyEntity;
import com.notier.rateService.RateService;
import com.notier.repository.CurrencyRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RateController {

    private final RateService rateService;
    private final CurrencyRepository currencyRepository;

    // 전송 테스트용 컨트롤러 - 하위 getCurrentCurrency 내부 서비스에 포함돼서 실제 서비스 사용 X
    @GetMapping("/notice-us")
    public String alarmUsChange() {
        CurrencyEntity currencyEntity = currencyRepository.findCurrencyEntityByTicker("USD")
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 국가입니다"));
        rateService.sendCurrencyMessage(currencyEntity);
        return "us dollar~~";
    }

    /**
     * 외부 api 콜해서 환율값 가져오는 api
     * 이후, 카프카에 통화별 환율 메시지 전송
     */
    @GetMapping("/batch/current-currency")
    public ResponseEntity<List<ExchangeResponseDto>> getCurrentCurrencyAll() {
        List<ExchangeResponseDto> responseDtoList = rateService.callInternalCurrencyApi();
        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * 특정 통화 환율 갱신하는 api
     * db에 저장된 기준값을 기준으로 -10 ~ +10 까지 랜덤으로 값을 줌
     */
    @GetMapping("/current-currency/{ticker}")
    public ResponseEntity<CurrentCurrencyResponseDto> getCurrentCurrency(@PathVariable("ticker") String ticker) {
        CurrentCurrencyResponseDto responseDto = rateService.modifyCurrentCurrency(ticker);
        return ResponseEntity.ok(responseDto);
    }
}