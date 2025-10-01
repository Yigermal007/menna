package com.example.mastercardsend.health;

import com.example.mastercardsend.config.MastercardSendProperties;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("mastercardSend")
public class SendApiHealthIndicator implements HealthIndicator {

    private final OkHttpClient client;
    private final MastercardSendProperties props;

    public SendApiHealthIndicator(OkHttpClient client, MastercardSendProperties props) {
        this.client = client;
        this.props = props;
    }

    @Override
    public Health health() {
        try {
            HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder().build();
            Request req = new Request.Builder().url(url).head().build();
            try (Response resp = client.newCall(req).execute()) {
                if (resp.isSuccessful()) {
                    return Health.up().withDetail("baseUrl", props.getBaseUrl()).build();
                }
                return Health.down().withDetail("status", resp.code()).build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}

