package com.example.mastercardsend.util;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Request;

import java.net.URI;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OkHttpMetricsEventListener extends EventListener {

    private final MeterRegistry registry;
    private final Clock clock;
    private final ConcurrentMap<Call, Instant> startTimes = new ConcurrentHashMap<>();

    public OkHttpMetricsEventListener(MeterRegistry registry) {
        this(registry, Clock.systemUTC());
    }

    public OkHttpMetricsEventListener(MeterRegistry registry, Clock clock) {
        this.registry = registry;
        this.clock = clock;
    }

    @Override
    public void callStart(Call call) {
        startTimes.put(call, Instant.now(clock));
    }

    @Override
    public void callEnd(Call call) {
        record(call, "OK");
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        record(call, ioe.getClass().getSimpleName());
    }

    private void record(Call call, String exception) {
        Instant start = startTimes.remove(call);
        if (start == null) return;
        Request req = call.request();
        URI uri = req.url().uri();
        Tags tags = Tags.of(
            Tag.of("method", req.method()),
            Tag.of("host", uri.getHost() == null ? "" : uri.getHost()),
            Tag.of("exception", exception)
        );
        Timer.builder("http.client.requests")
            .tags(tags)
            .register(registry)
            .record(Duration.between(start, Instant.now(clock)));
    }
}

