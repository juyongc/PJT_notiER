package com.notier.aop;

import com.notier.rateService.SlackService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlingAspect {

    private final SlackService slackService;

    @AfterThrowing(pointcut = "execution(* com.notier..*(..))", throwing = "ex")
    public void logException(IllegalArgumentException ex) {
        // 예외를 로깅합니다.
        log.error("Exception caught: ", ex);

        // 추가로 직원에게 알림을 보내는 로직을 여기에 추가할 수 있습니다.
        // 예를 들어 이메일, SMS, 또는 슬랙 메시지 등을 보낼 수 있습니다.
        slackService.sendSlackMessage(
            "에러 발생: " + ex.getMessage() + "\n" + "에러 정보: " + Arrays.toString(ex.getStackTrace())
        );
    }


}
