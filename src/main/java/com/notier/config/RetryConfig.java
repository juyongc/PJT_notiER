package com.notier.config;

import jakarta.persistence.OptimisticLockException;
import java.util.HashMap;
import org.hibernate.StaleObjectStateException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    @Bean(name = "randomRetryTemplate")
    public RetryTemplate randomRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        UniformRandomBackOffPolicy randomBackOffPolicy = new UniformRandomBackOffPolicy();
        randomBackOffPolicy.setMinBackOffPeriod(200);
        randomBackOffPolicy.setMaxBackOffPeriod(750);
        retryTemplate.setBackOffPolicy(randomBackOffPolicy);

        HashMap<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(OptimisticLockException.class, true);
        retryableExceptions.put(ObjectOptimisticLockingFailureException.class, true);
        retryableExceptions.put(StaleObjectStateException.class, true);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(5, retryableExceptions);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        return retryTemplate;
    }
}
