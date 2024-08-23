package com.notier.viewService;

import com.notier.dto.CreateCouponRequestDto;
import com.notier.entity.CouponCountEntity;
import com.notier.entity.CouponEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.entity.MemberCouponMapEntity;
import com.notier.entity.MemberEntity;
import com.notier.repository.CouponCountRepository;
import com.notier.repository.CouponRepository;
import com.notier.repository.MemberCouponMapRepository;
import com.notier.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class CouponOptimisticService {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final MemberCouponMapRepository memberCouponMapRepository;

    private final RetryTemplate retryTemplate;

//    @Transactional
//    public Boolean issueCouponWithRetry(CreateCouponRequestDto createCouponRequestDto) {
//        return retryTemplate.execute(context -> issueCoupon(createCouponRequestDto));
//    }

    //    @Transactional
    public Boolean issueCoupon(CreateCouponRequestDto createCouponRequestDto) {

        Long userId = Long.valueOf(createCouponRequestDto.getUserId());
        Long couponId = Long.valueOf(createCouponRequestDto.getCouponId());
        MemberEntity memberEntity = memberRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다"));

        CouponEntity couponEntity = couponRepository.findById(couponId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 쿠폰입니다"));

        CouponCountEntity couponCountEntity = couponCountRepository.findCouponCountEntityByCouponEntity(
            couponEntity);

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

    public CouponEntity findCouponEntityByCurrencyEntity(CurrencyEntity currencyEntity) {

        List<CouponEntity> couponEntityList = couponRepository.findCouponEntitiesByCurrencyEntity(
            currencyEntity);

        return couponEntityList.getFirst();
    }

}
