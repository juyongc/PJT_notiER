package com.notier.repository;

import com.notier.entity.CouponCountEntity;
import com.notier.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponCountRepository extends JpaRepository<CouponCountEntity, Long> {

    CouponCountEntity findCouponCountEntityByCouponEntity(CouponEntity couponEntity);

}
