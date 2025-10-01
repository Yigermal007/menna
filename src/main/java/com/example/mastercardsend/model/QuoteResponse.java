package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class QuoteResponse extends Extensible {
    @Schema(example = "9.75")
    private String payoutAmount;
    @Schema(example = "USD")
    private String payoutCurrency;

    public String getPayoutAmount() { return payoutAmount; }
    public void setPayoutAmount(String payoutAmount) { this.payoutAmount = payoutAmount; }
    public String getPayoutCurrency() { return payoutCurrency; }
    public void setPayoutCurrency(String payoutCurrency) { this.payoutCurrency = payoutCurrency; }
}

