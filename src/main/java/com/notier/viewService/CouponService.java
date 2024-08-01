package com.notier.viewService;

import com.notier.dto.CreateCouponRequestDto;
import com.notier.entity.CouponEntity;
import com.notier.entity.MemberCouponMapEntity;
import com.notier.entity.MemberEntity;
import com.notier.repository.CouponRepository;
import com.notier.repository.MemberCouponMapRepository;
import com.notier.repository.MemberRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CouponService {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponMapRepository memberCouponMapRepository;

    public Boolean issueCoupon(CreateCouponRequestDto createCouponRequestDto) {

        Long userId = Long.valueOf(createCouponRequestDto.getUserId());
        Long couponId = Long.valueOf(createCouponRequestDto.getCouponId());

        MemberEntity memberEntity = memberRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다"));

        CouponEntity couponEntity = couponRepository.findById(couponId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 쿠폰입니다"));

        if (couponEntity.getLimitNumber() > memberCouponMapRepository.countMemberCouponMapEntitiesByCouponEntity(
            couponEntity)) {

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
