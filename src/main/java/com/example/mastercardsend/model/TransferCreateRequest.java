package com.example.mastercardsend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "TransferCreateRequest", description = "Create a transfer request")
public class TransferCreateRequest extends Extensible {

    @Schema(example = "10.00")
    @NotNull
    private String amount;

    @Schema(example = "USD")
    @NotNull
    private String currency;

    @Schema(example = "recipient-123")
    @NotNull
    private String recipientId;

    private Money transferAmount;
    private Participant sender;
    private Participant recipient;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize request", e);
        }
    }

    public Money getTransferAmount() { return transferAmount; }
    public void setTransferAmount(Money transferAmount) { this.transferAmount = transferAmount; }
    public Participant getSender() { return sender; }
    public void setSender(Participant sender) { this.sender = sender; }
    public Participant getRecipient() { return recipient; }
    public void setRecipient(Participant recipient) { this.recipient = recipient; }
}

