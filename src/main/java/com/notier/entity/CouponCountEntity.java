package com.notier.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "COUPON_COUNTER")
public class CouponCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private CouponEntity couponEntity;

    private Integer issuedCount;

//    @Version
//    private Integer version;

    public void increaseIssuedCount() {
        this.issuedCount++;
    }

    @Override
    public String toString() {
        return "CouponCountEntity{" +
            "id=" + id +
            ", couponEntity=" + couponEntity +
            ", issuedCount=" + issuedCount +
            '}';
    }
}
