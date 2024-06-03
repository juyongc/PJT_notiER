package com.notier.controller;

import com.notier.rateService.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 직원들이 내부 데이터 세팅을 위해 사용하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange")
public class BackofficeController {

    private final ExchangeService exchangeService;

    @GetMapping("/init")
    public ResponseEntity<String> getExchangeInitData() {
        exchangeService.callExchangeOpenApi();
        return ResponseEntity.ok("Success");
    }

}
