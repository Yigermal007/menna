package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class QuoteRequest extends Extensible {

    @NotNull
    private Money amount;

    @Schema(example = "US")
    private String destinationCountry;

    public Money getAmount() { return amount; }
    public void setAmount(Money amount) { this.amount = amount; }
    public String getDestinationCountry() { return destinationCountry; }
    public void setDestinationCountry(String destinationCountry) { this.destinationCountry = destinationCountry; }
}

