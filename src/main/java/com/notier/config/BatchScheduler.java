package com.notier.config;

import com.notier.rateService.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final RedisService redisService;

    //    @Scheduled(cron = "0 * * * * *")    // 매분 실행
    public void runExchangeBatchScheduler() {
        try {
            Job callJob = jobRegistry.getJob("callInternalExchangeApiJob");
            jobLauncher.run(callJob,
                new JobParametersBuilder().addString("jobName", "callExchange" + System.currentTimeMillis())
                    .toJobParameters());
        } catch (NoSuchJobException | JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void resetAlarmsAtMidNight() {
        redisService.resetTodayAlarms();
    }

}
