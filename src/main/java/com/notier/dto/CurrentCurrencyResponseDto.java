package com.notier.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CurrentCurrencyResponseDto {

    private String country;
    private Long exchangeRate;

}


