package com.example.mastercardsend.config;

import com.example.mastercardsend.client.RetryableSendApiException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public RetryConfigCustomizer sendApiRetryCustomizer() {
        return RetryConfigCustomizer.of("sendApi", builder -> builder
            .retryOnException(ex -> ex instanceof RetryableSendApiException)
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(200))
        );
    }

    @Bean
    public io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer sendApiCbCustomizer() {
        return io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer.of(
            "sendApi",
            builder -> builder.slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50f)
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build()
        );
    }
}

