package com.notier.backOffice;

import java.util.List;
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

    private final BackofficeService backofficeService;

    @GetMapping("/init")
    public ResponseEntity<String> getExchangeInitData() {
        backofficeService.callExchangeOpenApi();
        return ResponseEntity.ok("Success");
    }

    /**
     * 모의로 만든 임의로 환율 값 주는 내부 api~
     */
    @GetMapping("/internal")
    public ResponseEntity<List<BackofficeCurrencyResponseDto>> getCurrentExchangeData() {
        List<BackofficeCurrencyResponseDto> responseDtoList = backofficeService.modifyAllCurrentCurrency();
        return ResponseEntity.ok(responseDtoList);
    }

}
