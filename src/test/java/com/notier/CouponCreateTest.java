package com.notier;

import com.notier.entity.CouponCountEntity;
import com.notier.entity.CouponEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.repository.CouponCountRepository;
import com.notier.repository.CouponRepository;
import com.notier.repository.CurrencyRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
//@Transactional
class CouponCreateTest {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponCountRepository couponCountRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    /**
     * 통화별 환율 쿠폰 발급
     */
    @Test
    void couponCreate() {

        List<CurrencyEntity> currencyEntities = currencyRepository.findAll();

        for (CurrencyEntity currencyEntity : currencyEntities) {

            CouponEntity couponEntity = CouponEntity.builder()
                .currencyEntity(currencyEntity)
                .explanation("welcome coupon")
                .expirationPeriod(7L)
                .limitNumber(500)
                .salePercent(30)
                .build();

            couponRepository.save(couponEntity);

            CouponCountEntity couponCountEntity = CouponCountEntity.builder()
                .couponEntity(couponEntity)
                .issuedCount(0)
                .build();

            couponCountRepository.save(couponCountEntity);
        }

        Assertions.assertThat((long) couponRepository.findAll().size()).isEqualTo(currencyEntities.size());
    }

    @Test
    void couponCountCreate() {

        List<CouponEntity> couponEntities = couponRepository.findAll();

        for (CouponEntity couponEntity : couponEntities) {

            CouponCountEntity couponCountEntity = CouponCountEntity.builder()
                .couponEntity(couponEntity)
                .issuedCount(0)
                .build();

            couponCountRepository.save(couponCountEntity);

        }

    }

}
