package com.example.mastercardsend.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class MetricsConfig {

    private final MeterRegistry meterRegistry;
    private final OkHttpClient client;

    public MetricsConfig(MeterRegistry meterRegistry, OkHttpClient client) {
        this.meterRegistry = meterRegistry;
        this.client = client;
    }

    @PostConstruct
    public void init() {
        // Basic meter to confirm app is emitting
        meterRegistry.counter("app.startup", Tags.of("module", "mastercard-send")).increment();
        // Note: OkHttp doesn't auto-bind; add custom metrics as needed in client calls or use a custom EventListener.
    }
}

