package com.notier.repository;

import com.notier.entity.CouponEntity;
import com.notier.entity.MemberCouponMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCouponMapRepository extends JpaRepository<MemberCouponMapEntity, Long> {

    long countMemberCouponMapEntitiesByCouponEntity(CouponEntity couponEntity);

}
