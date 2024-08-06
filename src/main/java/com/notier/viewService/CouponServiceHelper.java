package com.notier.viewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class CouponServiceHelper {

//    private final CouponService couponService;
//
//    private final RetryTemplate retryTemplate;
//
//    @Transactional
//    public Boolean issueCouponWithRetry(CreateCouponRequestDto createCouponRequestDto) {
//
//        return retryTemplate.execute(context -> {
//            log.info("Retry attempt: {}", context.getRetryCount());
//            try {
//                return couponService.issueCoupon(createCouponRequestDto);
//            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException | StaleObjectStateException e) {
//                log.error("Optimistic lock exception, retrying...", e);
//                throw e;
//            }
//        });
//    }
}
