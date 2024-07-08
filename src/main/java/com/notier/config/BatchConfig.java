package com.notier.config;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Value("${api.exchange-inner-base-url}")
    private String exchangeInnerUrl;

    @Bean
    public Tasklet callExchangeApiTasklet(RestTemplate restTemplate) {
        return ((contribution, chunkContext) -> {
            URI uri = UriComponentsBuilder.fromHttpUrl(exchangeInnerUrl)
                .build()
                .toUri();
            String res = restTemplate.getForObject(uri, String.class);
            log.info("res = " + res);
            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public Step step1(RestTemplate restTemplate) {
        return new StepBuilder("callInternalExchangeApiStep", jobRepository)
            .tasklet(callExchangeApiTasklet(restTemplate), platformTransactionManager)
            .build();
    }

    @Bean
    public Job callExchangeApiJob(Step step1) {
        return new JobBuilder("callInternalExchangeApiJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(step1)
            .build();
    }
}
