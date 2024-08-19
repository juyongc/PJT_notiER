package com.notier.viewService;

import com.notier.dto.CreateCouponRequestDto;
import com.notier.entity.CouponEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.entity.MemberEntity;
import com.notier.repository.CouponCountRepository;
import com.notier.repository.CouponRepository;
import com.notier.repository.MemberCouponMapRepository;
import com.notier.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
//@Transactional
@Service
public class CouponService {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final MemberCouponMapRepository memberCouponMapRepository;
    private final RedissonClient redissonClient;
    private final CouponLockService couponLockService;

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Redisson distributed Lock!!!
     */
    @Transactional
    public Boolean issueCoupon(CreateCouponRequestDto createCouponRequestDto) {

        Long userId = Long.valueOf(createCouponRequestDto.getUserId());
        Long couponId = Long.valueOf(createCouponRequestDto.getCouponId());

        String lockKey = "couponLock" + couponId;
        RLock lock = redissonClient.getLock(lockKey);

        try {

            boolean isLocked = lock.tryLock(10, 2, TimeUnit.SECONDS);

            if (!isLocked) {
                log.warn("락 획득에 실패했습니다! 쿠폰 ID : {}", couponId);
                return Boolean.FALSE;
            }

            log.info("Starting Point : user = {}", userId);
            log.info("who get a lock? INFO : coupon = {}, user = {}, lockkey = {}", couponId, userId, lockKey);

            MemberEntity memberEntity = memberRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다"));

            CouponEntity couponEntity = couponRepository.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 쿠폰입니다"));

            return couponLockService.lockCouponCounter(memberEntity, couponEntity);

        } catch (InterruptedException e) {
            log.error("락 획득과정에서 문제가 생겼습니다!", e);
            return Boolean.FALSE;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("who unlock a lock? INFO : coupon = {}, user = {}, lockkey = {}", couponId, userId, lockKey);
            }
        }
    }

    public CouponEntity findCouponEntityByCurrencyEntity(CurrencyEntity currencyEntity) {

        List<CouponEntity> couponEntityList = couponRepository.findCouponEntitiesByCurrencyEntity(
            currencyEntity);

        return couponEntityList.getFirst();
    }

}
