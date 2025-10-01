package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class Money {

    @Schema(example = "10.00")
    @NotBlank
    private String amount;

    @Schema(example = "USD")
    @NotBlank
    private String currency;

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}

