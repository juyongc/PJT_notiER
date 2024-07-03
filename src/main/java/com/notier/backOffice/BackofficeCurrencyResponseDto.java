package com.notier.backOffice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BackofficeCurrencyResponseDto {

    private String ticker;
    private String explanation;
    private Long exchangeRate;

}
