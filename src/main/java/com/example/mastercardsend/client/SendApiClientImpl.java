package com.example.mastercardsend.client;

import com.example.mastercardsend.config.MastercardSendProperties;
import com.example.mastercardsend.model.TransferCreateRequest;
import com.example.mastercardsend.model.TransferCreateResponse;
import com.example.mastercardsend.model.TransferStatusResponse;
import com.example.mastercardsend.model.QuoteRequest;
import com.example.mastercardsend.model.QuoteResponse;
import com.example.mastercardsend.model.EligibilityRequest;
import com.example.mastercardsend.model.EligibilityResponse;
import com.example.mastercardsend.model.TransferCancelResponse;
import com.example.mastercardsend.model.TransferReverseResponse;
import com.example.mastercardsend.model.TransferDetailsResponse;
import com.example.mastercardsend.model.PaymentTransferRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendApiClientImpl implements SendApiClient {

    private static final Logger log = LoggerFactory.getLogger(SendApiClientImpl.class);
    private static final MediaType JSON = MediaType.parse("application/json");

    private final OkHttpClient httpClient;
    private final MastercardSendProperties props;

    public SendApiClientImpl(OkHttpClient httpClient, MastercardSendProperties props) {
        this.httpClient = httpClient;
        this.props = props;
    }

    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public TransferCreateResponse createTransfer(TransferCreateRequest request) {
        return createTransfer(request, null);
    }
    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public TransferCreateResponse createTransfer(TransferCreateRequest request, String idempotencyKey) {
        String path = props.getApiPaths().getCreateTransfer()
            .replace("{partnerId}", props.getPartnerId());
        HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder()
            .addEncodedPathSegments(path.replaceFirst("^/", ""))
            .build();
        RequestBody body = RequestBody.create(request.toJson(), JSON);
        Request.Builder rb = new Request.Builder()
            .url(url)
            .post(body)
            .header("Content-Type", "application/json");
        // Mastercard common headers
        rb.header("User-Agent", props.getUserAgent());
        rb.header("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)));
        rb.header("X-Request-Id", java.util.UUID.randomUUID().toString());
        if (props.getAcceptLanguage() != null) rb.header("Accept-Language", props.getAcceptLanguage());
        if (idempotencyKey != null && !idempotencyKey.isBlank()) rb.header("Idempotency-Key", idempotencyKey);
        if (props.getClientReferenceId() != null && !props.getClientReferenceId().isBlank()) rb.header("X-Client-Reference-Id", props.getClientReferenceId());
        if (props.getPartnerId() != null && !props.getPartnerId().isBlank()) rb.header("X-Partner-Id", props.getPartnerId());
        String correlationId = MDC.get("correlationId");
        if (correlationId != null) rb.header("X-Correlation-ID", correlationId);
        Request httpRequest = rb.build();
        try (Response response = httpClient.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                if (response.code() >= 500 || response.code() == 429) {
                    throw new RetryableSendApiException(response.code(), responseBody);
                }
                throw SendApiException.from(response.code(), responseBody);
            }
            return TransferCreateResponse.fromJson(responseBody);
        } catch (IOException e) {
            throw new RetryableSendApiException("I/O error calling Send API", e);
        }
    }

    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public TransferCreateResponse createTransfer(PaymentTransferRequest request) {
        return createTransfer(request, null);
    }

    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public TransferCreateResponse createTransfer(PaymentTransferRequest request, String idempotencyKey) {
        String path = props.getApiPaths().getCreateTransfer()
            .replace("{partnerId}", props.getPartnerId());
        HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder()
            .addEncodedPathSegments(path.replaceFirst("^/", ""))
            .build();
        RequestBody body = RequestBody.create(toJson(request), JSON);
        Request.Builder rb = new Request.Builder()
            .url(url)
            .post(body)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", props.getUserAgent())
            .header("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)))
            .header("X-Request-Id", java.util.UUID.randomUUID().toString());
        if (props.getAcceptLanguage() != null) rb.header("Accept-Language", props.getAcceptLanguage());
        if (idempotencyKey != null && !idempotencyKey.isBlank()) rb.header("Idempotency-Key", idempotencyKey);
        if (props.getClientReferenceId() != null && !props.getClientReferenceId().isBlank()) rb.header("X-Client-Reference-Id", props.getClientReferenceId());
        if (props.getPartnerId() != null && !props.getPartnerId().isBlank()) rb.header("X-Partner-Id", props.getPartnerId());
        String correlationId = org.slf4j.MDC.get("correlationId");
        if (correlationId != null) rb.header("X-Correlation-ID", correlationId);
        try (Response response = httpClient.newCall(rb.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                if (response.code() >= 500 || response.code() == 429) throw new RetryableSendApiException(response.code(), responseBody);
                throw SendApiException.from(response.code(), responseBody);
            }
            return TransferCreateResponse.fromJson(responseBody);
        } catch (IOException e) {
            throw new RetryableSendApiException("I/O error calling Send API", e);
        }
    }

    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public TransferStatusResponse getTransferStatus(String transferId) {
        String path = props.getApiPaths().getTransferStatus()
            .replace("{partnerId}", props.getPartnerId())
            .replace("{id}", transferId);
        HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder()
            .addEncodedPathSegments(path.replaceFirst("^/", ""))
            .build();
        Request.Builder rb = new Request.Builder()
            .url(url)
            .get()
            .header("Accept", "application/json");
        rb.header("User-Agent", props.getUserAgent());
        rb.header("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)));
        rb.header("X-Request-Id", java.util.UUID.randomUUID().toString());
        if (props.getAcceptLanguage() != null) rb.header("Accept-Language", props.getAcceptLanguage());
        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
            rb.header("X-Correlation-ID", correlationId);
        }
        Request httpRequest = rb.build();
        try (Response response = httpClient.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                if (response.code() >= 500 || response.code() == 429) {
                    throw new RetryableSendApiException(response.code(), responseBody);
                }
                throw SendApiException.from(response.code(), responseBody);
            }
            return TransferStatusResponse.fromJson(responseBody);
        } catch (IOException e) {
            throw new RetryableSendApiException("I/O error calling Send API", e);
        }
    }

    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public QuoteResponse getQuote(QuoteRequest request) {
        String path = props.getApiPaths().getQuote()
            .replace("{partnerId}", props.getPartnerId());
        HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder()
            .addEncodedPathSegments(path.replaceFirst("^/", ""))
            .build();
        RequestBody body = RequestBody.create(toJson(request), JSON);
        Request.Builder rb = new Request.Builder().url(url).post(body)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", props.getUserAgent())
            .header("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)))
            .header("X-Request-Id", java.util.UUID.randomUUID().toString());
        if (props.getAcceptLanguage() != null) rb.header("Accept-Language", props.getAcceptLanguage());
        String correlationId = org.slf4j.MDC.get("correlationId");
        if (correlationId != null) rb.header("X-Correlation-ID", correlationId);
        try (Response response = httpClient.newCall(rb.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                if (response.code() >= 500 || response.code() == 429) throw new RetryableSendApiException(response.code(), responseBody);
                throw SendApiException.from(response.code(), responseBody);
            }
            return fromJson(responseBody, QuoteResponse.class);
        } catch (IOException e) {
            throw new RetryableSendApiException("I/O error calling Send API", e);
        }
    }

    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public EligibilityResponse checkEligibility(EligibilityRequest request) {
        String path = props.getApiPaths().getEligibility()
            .replace("{partnerId}", props.getPartnerId());
        HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder()
            .addEncodedPathSegments(path.replaceFirst("^/", ""))
            .build();
        RequestBody body = RequestBody.create(toJson(request), JSON);
        Request.Builder rb = new Request.Builder().url(url).post(body)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", props.getUserAgent())
            .header("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)))
            .header("X-Request-Id", java.util.UUID.randomUUID().toString());
        if (props.getAcceptLanguage() != null) rb.header("Accept-Language", props.getAcceptLanguage());
        String correlationId = org.slf4j.MDC.get("correlationId");
        if (correlationId != null) rb.header("X-Correlation-ID", correlationId);
        try (Response response = httpClient.newCall(rb.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                if (response.code() >= 500 || response.code() == 429) throw new RetryableSendApiException(response.code(), responseBody);
                throw SendApiException.from(response.code(), responseBody);
            }
            return fromJson(responseBody, EligibilityResponse.class);
        } catch (IOException e) {
            throw new RetryableSendApiException("I/O error calling Send API", e);
        }
    }

    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public TransferCancelResponse cancelTransfer(String transferId) {
        String path = props.getApiPaths().getCancelTransfer()
            .replace("{partnerId}", props.getPartnerId())
            .replace("{id}", transferId);
        HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder()
            .addEncodedPathSegments(path.replaceFirst("^/", ""))
            .build();
        Request.Builder rb = new Request.Builder().url(url).post(RequestBody.create(new byte[0], null))
            .header("Accept", "application/json")
            .header("User-Agent", props.getUserAgent())
            .header("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)))
            .header("X-Request-Id", java.util.UUID.randomUUID().toString());
        if (props.getAcceptLanguage() != null) rb.header("Accept-Language", props.getAcceptLanguage());
        String correlationId = org.slf4j.MDC.get("correlationId");
        if (correlationId != null) rb.header("X-Correlation-ID", correlationId);
        try (Response response = httpClient.newCall(rb.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                if (response.code() >= 500 || response.code() == 429) throw new RetryableSendApiException(response.code(), responseBody);
                throw SendApiException.from(response.code(), responseBody);
            }
            return fromJson(responseBody, TransferCancelResponse.class);
        } catch (IOException e) {
            throw new RetryableSendApiException("I/O error calling Send API", e);
        }
    }

    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public TransferReverseResponse reverseTransfer(String transferId) {
        String path = props.getApiPaths().getReverseTransfer()
            .replace("{partnerId}", props.getPartnerId())
            .replace("{id}", transferId);
        HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder()
            .addEncodedPathSegments(path.replaceFirst("^/", ""))
            .build();
        Request.Builder rb = new Request.Builder().url(url).post(RequestBody.create(new byte[0], null))
            .header("Accept", "application/json")
            .header("User-Agent", props.getUserAgent())
            .header("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)))
            .header("X-Request-Id", java.util.UUID.randomUUID().toString());
        if (props.getAcceptLanguage() != null) rb.header("Accept-Language", props.getAcceptLanguage());
        String correlationId = org.slf4j.MDC.get("correlationId");
        if (correlationId != null) rb.header("X-Correlation-ID", correlationId);
        try (Response response = httpClient.newCall(rb.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                if (response.code() >= 500 || response.code() == 429) throw new RetryableSendApiException(response.code(), responseBody);
                throw SendApiException.from(response.code(), responseBody);
            }
            return fromJson(responseBody, TransferReverseResponse.class);
        } catch (IOException e) {
            throw new RetryableSendApiException("I/O error calling Send API", e);
        }
    }

    @Override
    @Retry(name = "sendApi")
    @CircuitBreaker(name = "sendApi")
    public TransferDetailsResponse getTransferDetails(String transferId) {
        String path = props.getApiPaths().getTransferDetails()
            .replace("{partnerId}", props.getPartnerId())
            .replace("{id}", transferId);
        HttpUrl url = HttpUrl.parse(props.getBaseUrl()).newBuilder()
            .addEncodedPathSegments(path.replaceFirst("^/", ""))
            .build();
        Request.Builder rb = new Request.Builder().url(url).get()
            .header("Accept", "application/json")
            .header("User-Agent", props.getUserAgent())
            .header("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)))
            .header("X-Request-Id", java.util.UUID.randomUUID().toString());
        if (props.getAcceptLanguage() != null) rb.header("Accept-Language", props.getAcceptLanguage());
        String correlationId = org.slf4j.MDC.get("correlationId");
        if (correlationId != null) rb.header("X-Correlation-ID", correlationId);
        try (Response response = httpClient.newCall(rb.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                if (response.code() >= 500 || response.code() == 429) throw new RetryableSendApiException(response.code(), responseBody);
                throw SendApiException.from(response.code(), responseBody);
            }
            return fromJson(responseBody, TransferDetailsResponse.class);
        } catch (IOException e) {
            throw new RetryableSendApiException("I/O error calling Send API", e);
        }
    }

    private static String toJson(Object o) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(o);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize request", e);
        }
    }

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, type);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse response", e);
        }
    }
}

