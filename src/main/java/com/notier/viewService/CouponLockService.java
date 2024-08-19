package com.notier.viewService;

import com.notier.entity.CouponCountEntity;
import com.notier.entity.CouponEntity;
import com.notier.entity.MemberCouponMapEntity;
import com.notier.entity.MemberEntity;
import com.notier.repository.CouponCountRepository;
import com.notier.repository.CouponRepository;
import com.notier.repository.MemberCouponMapRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CouponLockService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final MemberCouponMapRepository memberCouponMapRepository;


    /**
     * Redisson distributed Lock!!!
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @Transactional
    public Boolean lockCouponCounter(MemberEntity memberEntity, CouponEntity couponEntity) {

        CouponCountEntity couponCountEntity = couponCountRepository.findCouponCountEntityByCouponEntity(couponEntity);

        if (couponEntity.getLimitNumber() > couponCountEntity.getIssuedCount()) {

            couponCountEntity.increaseIssuedCount();

            log.info("CouponCountEntity = {}", couponCountEntity);
            log.info("check issuedCount = {}", couponCountEntity.getIssuedCount());

            couponCountRepository.save(couponCountEntity);

            MemberCouponMapEntity memberCouponMapEntity = MemberCouponMapEntity.builder()
                .couponEntity(couponEntity)
                .memberEntity(memberEntity)
                .expirationDate(LocalDate.now().plusDays(couponEntity.getExpirationPeriod()))
                .isUsed(false)
                .build();

            memberCouponMapRepository.save(memberCouponMapEntity);

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

}
