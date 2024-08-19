package com.notier;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notier.dto.CreateCouponRequestDto;
import com.notier.entity.CouponEntity;
import com.notier.entity.CurrencyEntity;
import com.notier.viewService.CouponService;
import com.notier.viewService.CurrencyService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@Slf4j
class CouponConcurrencyTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CouponService couponService;

    @Test
    void couponConcurrencyTest() throws InterruptedException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        ExecutorService executorService = Executors.newFixedThreadPool(20);

        // 여러 스레드에서 동시에 issueCoupon 메서드 호출
        for (int i = 4; i < 24; i++) {
            final int userId = i;
            CurrencyEntity usdEntity = currencyService.findCurrencyEntityByTicker("USD");
            CouponEntity couponEntity = couponService.findCouponEntityByCurrencyEntity(usdEntity);
            Long couponEntityId = couponEntity.getId();

            executorService.submit(() -> {
                try {

                    CreateCouponRequestDto createCouponRequestDto = CreateCouponRequestDto.builder()
                        .couponId(couponEntityId.toString())
                        .userId(String.valueOf(userId))
                        .build();

                    mockMvc.perform(MockMvcRequestBuilders.post("/api/currency/coupon")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCouponRequestDto)))
                        .andExpect(status().isOk())
                        .andDo(result -> {
                            Boolean response = objectMapper.readValue(result.getResponse().getContentAsString(),
                                Boolean.class);
                            log.info("Coupon issue result for userId " + userId + ": " + response);
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // 스레드 풀 종료
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }
}
