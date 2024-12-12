package com.notier.viewService;

import com.notier.dto.CreateCouponRequestDto;
import com.notier.entity.CouponCountEntity;
import com.notier.entity.CouponEntity;
import com.notier.entity.MemberCouponMapEntity;
import com.notier.entity.MemberEntity;
import com.notier.rateService.RedisService;
import com.notier.repository.CouponCountRepository;
import com.notier.repository.CouponRepository;
import com.notier.repository.MemberCouponMapRepository;
import com.notier.repository.MemberRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CouponLockService {

    private final CouponCountRepository couponCountRepository;
    private final MemberCouponMapRepository memberCouponMapRepository;
    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final RedisService redisService;


    /**
     * redis 분산 락으로 동시성 제어 메서드
     */
    @Transactional
    public Boolean lockCouponCounter(MemberEntity memberEntity, CouponEntity couponEntity) {

        CouponCountEntity couponCountEntity = couponCountRepository.findCouponCountEntityByCouponEntity(couponEntity);

        if (couponEntity.getLimitNumber() > couponCountEntity.getIssuedCount()) {

            couponCountEntity.increaseIssuedCount();

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

    /**
     * 원자적 연산으로 쿠폰 발급 제어하는 메서드
     */
    @Transactional
    public Boolean lockCouponCounterAtomic(CreateCouponRequestDto createCouponRequestDto) {

        Long userId = Long.valueOf(createCouponRequestDto.getUserId());
        Long couponId = Long.valueOf(createCouponRequestDto.getCouponId());

        MemberEntity memberEntity = memberRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다"));

        CouponEntity couponEntity = couponRepository.findById(couponId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 쿠폰입니다"));

        if (redisService.getIssueCouponOnRedis(couponEntity.getId()) <= 0) {
            return Boolean.FALSE;
        }

        boolean isDecreased = redisService.decreaseIssuedCouponAmount(couponEntity.getId());

        if (!isDecreased) {
            return Boolean.FALSE;
        }

        try {
            MemberCouponMapEntity memberCouponMapEntity = MemberCouponMapEntity.builder()
                .couponEntity(couponEntity)
                .memberEntity(memberEntity)
                .expirationDate(LocalDate.now().plusDays(couponEntity.getExpirationPeriod()))
                .isUsed(false)
                .build();

            memberCouponMapRepository.save(memberCouponMapEntity);
        } catch (Exception e) {
            redisService.compensateIssuedCouponAmount(couponId);
            throw e;
        }

        return Boolean.TRUE;
    }

}
