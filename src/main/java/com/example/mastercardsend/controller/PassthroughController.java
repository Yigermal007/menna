package com.example.mastercardsend.controller;

import com.example.mastercardsend.config.MastercardSendProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
// note: use fully qualified okhttp3.RequestBody to avoid clash with Spring's @RequestBody
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider")
@Tag(name = "Provider passthrough")
public class PassthroughController {

    private static final MediaType JSON = MediaType.parse("application/json");

    private final OkHttpClient httpClient;
    private final MastercardSendProperties props;

    public PassthroughController(OkHttpClient httpClient, MastercardSendProperties props) {
        this.httpClient = httpClient;
        this.props = props;
    }

    @PostMapping("/transactions")
    @Operation(summary = "Raw passthrough to provider transactions create (JSON)")
    public ResponseEntity<String> passthrough(@org.springframework.web.bind.annotation.RequestBody String payload,
                                              @RequestHeader(value = "Idempotency-Key", required = false) String idem) throws Exception {
        String path = props.getApiPaths().getCreateTransfer()
            .replace("{partnerId}", props.getPartnerId());
        HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder()
            .addEncodedPathSegments(path.replaceFirst("^/", ""))
            .build();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(payload, JSON);
        Request.Builder rb = new Request.Builder().url(url).post(body)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", "mastercard-send-service/1.0")
            .header("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)))
            .header("X-Request-Id", java.util.UUID.randomUUID().toString());
        if (idem != null && !idem.isBlank()) rb.header("Idempotency-Key", idem);
        try (Response resp = httpClient.newCall(rb.build()).execute()) {
            String responseBody = resp.body() != null ? resp.body().string() : "";
            return ResponseEntity.status(resp.code()).body(responseBody);
        }
    }
}

