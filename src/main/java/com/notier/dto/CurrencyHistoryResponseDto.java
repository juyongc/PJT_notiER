package com.notier.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyHistoryResponseDto {

    private String ticker;
    private Long exchangeRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Override
    public String toString() {
        return "CurrencyHistoryResponseDto{" +
            "ticker='" + ticker + '\'' +
            ", exchangeRate=" + exchangeRate +
            ", date=" + date +
            '}';
    }
}
