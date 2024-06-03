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
@Table(name = "CURRENCY_LOGS")
public class CurrencyLogEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // currencyEntity와 관계를 가짐으로서 가질 수 있는 이점이 없다고 판단됨
    // 사용자가 선택할 때, 통화를 선택하게 될거라 country를 통해 바로 가져올 수 있도록 만드는 게 좋을 것 같음
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "currency_id")
//    private CurrencyEntity currencyEntity;

    private String ticker;
    private String explanation;
    private Long exchangeRate;
}
