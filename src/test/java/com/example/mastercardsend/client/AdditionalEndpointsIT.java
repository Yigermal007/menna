package com.example.mastercardsend.client;

import com.example.mastercardsend.config.MastercardSendProperties;
import com.example.mastercardsend.model.EligibilityRequest;
import com.example.mastercardsend.model.EligibilityResponse;
import com.example.mastercardsend.model.Money;
import com.example.mastercardsend.model.QuoteRequest;
import com.example.mastercardsend.model.QuoteResponse;
import com.example.mastercardsend.model.TransferCancelResponse;
import com.example.mastercardsend.model.TransferDetailsResponse;
import com.example.mastercardsend.model.TransferReverseResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.TestSocketUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

class AdditionalEndpointsIT {

    static WireMockServer wm;

    @BeforeAll
    static void setup() {
        wm = new WireMockServer(TestSocketUtils.findAvailableTcpPort());
        wm.start();
    }

    @AfterAll
    static void teardown() {
        wm.stop();
    }

    @Test
    void quoteEligibilityCancelReverseDetails() {
        // Quote
        wm.stubFor(post(urlEqualTo("/send/v1/partners/partner-123/quotes"))
            .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                .withBody("{\"payoutAmount\":\"9.75\",\"payoutCurrency\":\"USD\"}")));

        // Eligibility
        wm.stubFor(post(urlEqualTo("/send/v1/partners/partner-123/eligibility"))
            .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                .withBody("{\"eligible\":true}")));

        // Cancel
        wm.stubFor(post(urlEqualTo("/send/v1/partners/partner-123/transactions/t1/cancel"))
            .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                .withBody("{\"status\":\"CANCELLED\"}")));

        // Reverse
        wm.stubFor(post(urlEqualTo("/send/v1/partners/partner-123/transactions/t1/reverse"))
            .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                .withBody("{\"status\":\"REVERSED\"}")));

        // Details
        wm.stubFor(get(urlEqualTo("/send/v1/partners/partner-123/transactions/t1/details"))
            .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                .withBody("{\"transferId\":\"t1\",\"status\":\"COMPLETED\"}")));

        ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                "mastercard.send.base-url=http://localhost:" + wm.port(),
                "mastercard.send.signing-disabled=true",
                "mastercard.send.partner-id=partner-123"
            );

        runner.run(ctx -> {
            SendApiClient client = ctx.getBean(SendApiClient.class);

            QuoteRequest qr = new QuoteRequest();
            Money money = new Money();
            money.setAmount("10.00");
            money.setCurrency("USD");
            qr.setAmount(money);
            QuoteResponse qres = client.getQuote(qr);
            assertThat(qres.getPayoutAmount()).isEqualTo("9.75");

            EligibilityRequest er = new EligibilityRequest();
            er.setRecipientPhone("+14155550123");
            EligibilityResponse eres = client.checkEligibility(er);
            assertThat(eres.getEligible()).isTrue();

            TransferCancelResponse cres = client.cancelTransfer("t1");
            assertThat(cres.getStatus()).isEqualTo("CANCELLED");

            TransferReverseResponse rres = client.reverseTransfer("t1");
            assertThat(rres.getStatus()).isEqualTo("REVERSED");

            TransferDetailsResponse dres = client.getTransferDetails("t1");
            assertThat(dres.getTransferId()).isEqualTo("t1");
        });
    }

    @Configuration
    static class TestConfig {
        @Bean MastercardSendProperties props() { return new MastercardSendProperties(); }
        @Bean OkHttpClient client() { return new OkHttpClient(); }
        @Bean SendApiClient sendApiClient(OkHttpClient c, MastercardSendProperties p) { return new SendApiClientImpl(c, p); }
    }
}

