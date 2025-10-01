package com.example.mastercardsend.client;

import com.example.mastercardsend.config.MastercardSendProperties;
import com.example.mastercardsend.model.TransferCreateRequest;
import com.example.mastercardsend.model.TransferCreateResponse;
import com.example.mastercardsend.model.TransferStatusResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.TestSocketUtils;

import okhttp3.OkHttpClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

class SendApiClientImplIT {

    static WireMockServer wm;

    @BeforeAll
    static void setup() {
        int port = TestSocketUtils.findAvailableTcpPort();
        wm = new WireMockServer(port);
        wm.start();
    }

    @AfterAll
    static void teardown() {
        wm.stop();
    }

    @Test
    void createAndGetStatus() {
        wm.stubFor(post(urlEqualTo("/send/v1/partners/transactions"))
            .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                .withBody("{\"transferId\":\"t123\",\"status\":\"PENDING\"}")));

        wm.stubFor(get(urlEqualTo("/send/v1/partners/transactions/t123"))
            .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                .withBody("{\"transferId\":\"t123\",\"status\":\"COMPLETED\"}")));

        ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                "mastercard.send.base-url=http://localhost:" + wm.port(),
                "mastercard.send.signing-disabled=true"
            );

        runner.run(ctx -> {
            SendApiClient client = ctx.getBean(SendApiClient.class);

            TransferCreateRequest req = new TransferCreateRequest();
            req.setAmount("10.00");
            req.setCurrency("USD");
            req.setRecipientId("r1");

            TransferCreateResponse created = client.createTransfer(req);
            assertThat(created.getTransferId()).isEqualTo("t123");

            TransferStatusResponse status = client.getTransferStatus("t123");
            assertThat(status.getStatus()).isEqualTo("COMPLETED");
        });
    }

    @Configuration
    static class TestConfig {
        @Bean
        MastercardSendProperties props() { return new MastercardSendProperties(); }
        @Bean
        OkHttpClient client() { return new OkHttpClient(); }
        @Bean
        SendApiClient sendApiClient(OkHttpClient c, MastercardSendProperties p) { return new SendApiClientImpl(c, p); }
    }
}

