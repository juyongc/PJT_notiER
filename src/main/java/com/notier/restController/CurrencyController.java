package com.notier.restController;

import com.notier.dto.CreateCouponRequestDto;
import com.notier.dto.CurrencyAllResponseDto;
import com.notier.dto.CurrencyHistoryResponseDto;
import com.notier.viewService.CouponService;
import com.notier.viewService.CouponServiceHelper;
import com.notier.viewService.CurrencyService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final CouponService couponService;
    private final CouponServiceHelper couponServiceHelper;
    private final RetryTemplate retryTemplate;

    @GetMapping("/all")
    public ResponseEntity<List<CurrencyAllResponseDto>> getCurrencyAll() {

        List<CurrencyAllResponseDto> currencyAllResponseDtos = currencyService.combineCurrencyAllResponseList();

        return ResponseEntity.ok(currencyAllResponseDtos);
    }


    /**
     * 사용자가 누르면 해당 통화의 과거 시세를 볼 수 있어야 함
     * 추후에는 매매기준율을 기반으로 개발할 예정
     * 현재는 임시적으로 지난 일주일간 데이터를 보여주는 방향으로 개발
     *
     * @return
     * @pathVariable ticker
     * @Dto => page, size
     */
    @GetMapping("/history/{ticker}")
    public ResponseEntity<Page<CurrencyHistoryResponseDto>> getCurrencyHistoryInfos(@PathVariable String ticker,
        @RequestParam int page, @RequestParam int size) {

        LocalDateTime testTime = LocalDateTime.of(2024, 6, 20, 0, 0, 0);

//        LocalDateTime aWeekAgo = LocalDateTime.now().minusDays(7);
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<CurrencyHistoryResponseDto> currencyHistoryInfoList = currencyService.findCurrencyHistoryInfoList(ticker,
            testTime, pageRequest);
//        Page<CurrencyHistoryResponseDto> currencyHistoryInfoList = currencyService.findCurrencyHistoryInfoList(ticker,
//            aWeekAgo, pageRequest);

        currencyHistoryInfoList.stream()
            .forEach(currencyHistoryResponseDto -> log.info(currencyHistoryResponseDto.toString()));

        return ResponseEntity.ok(currencyHistoryInfoList);
    }

    @PostMapping("/coupon/optimistic")
    public ResponseEntity<Boolean> createCouponOptimisticLock(
        @RequestBody CreateCouponRequestDto createCouponRequestDto) {

        Boolean issuedCoupon = retryTemplate.execute(context -> {
            log.info("Controller Retry attempt: {}", context.getRetryCount());
            try {
                return couponService.issueCoupon(createCouponRequestDto);
            } catch (ObjectOptimisticLockingFailureException e) {
                log.error("Controller Optimistic lock exception, retrying...", e);
                throw e;
            }
        });

        return ResponseEntity.ok(issuedCoupon);
    }


    @PostMapping("/coupon")
    public ResponseEntity<Boolean> createCoupon(@RequestBody CreateCouponRequestDto createCouponRequestDto) {

        Boolean issuedCoupon = couponService.issueCoupon(createCouponRequestDto);

        return ResponseEntity.ok(issuedCoupon);
    }

}