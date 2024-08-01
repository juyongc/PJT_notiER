package com.notier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyAllResponseDto {

    private String ticker;
    private String explanation;
    private Long exchangeRate;
    private Long couponId;
}
