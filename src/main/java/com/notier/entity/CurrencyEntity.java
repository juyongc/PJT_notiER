package com.notier.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CURRENCY")
public class CurrencyEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;
    private String explanation;
    private Long exchangeRate;

    public void updateExchangeRate(Long updatedRate) {
        this.exchangeRate = updatedRate;
    }

    @Override
    public String toString() {
        return "CurrencyEntity{" +
            "id=" + id +
            ", country='" + ticker + '\'' +
            ", rateUnit='" + explanation + '\'' +
            ", exchangeRate=" + exchangeRate +
            '}';
    }
}
