package com.notier.repository;

import com.notier.entity.CouponCountEntity;
import com.notier.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponCountRepository extends JpaRepository<CouponCountEntity, Long> {

    CouponCountEntity findCouponCountEntityByCouponEntity(CouponEntity couponEntity);

    @Query(value = "SELECT * FROM coupon_counter WHERE coupon_id = :couponId", nativeQuery = true)
    CouponCountEntity findCouponCountEntityByCouponIdNative(@Param("couponId") Long couponId);

}
