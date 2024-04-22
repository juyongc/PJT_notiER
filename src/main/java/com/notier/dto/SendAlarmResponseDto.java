package com.notier.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SendAlarmResponseDto {

    private Long memberId;
    private String memberName;
    private Long currencyId;
    private String country;
    private Long exchangeRate;

    @Override
    public String toString() {
        return "SendAlarmResponseDto{" +
            "memberId=" + memberId +
            ", memberName='" + memberName + '\'' +
            ", currencyId=" + currencyId +
            ", country='" + country + '\'' +
            ", exchangeRate=" + exchangeRate +
            '}';
    }
}
