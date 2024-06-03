package com.notier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.notier.utils.StringToLongDeserializer;
import com.notier.utils.TickerGetFirstThreeCharsDeserializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExchangeResponseDto {

    @JsonProperty("cur_unit")
    @JsonDeserialize(using = TickerGetFirstThreeCharsDeserializer.class)
    private String ticker;
    @JsonProperty("cur_nm")
    private String explanation;
    @JsonProperty("bkpr")
    @JsonDeserialize(using = StringToLongDeserializer.class)
    private Long exchangeRate;
}
