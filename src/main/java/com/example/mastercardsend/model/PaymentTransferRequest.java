package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class PaymentTransferRequest extends Extensible {

    @Schema(example = "req-123")
    private String requestId;

    @NotNull
    private Money transferAmount;

    @Schema(example = "P2P")
    private String paymentType;

    private Participant sender;
    private Participant recipient;

    @Schema(example = "urn:payment:card:pan:411111******1111")
    private String receivingAccountUri;

    @Schema(example = "urn:payment:card:pan:545454******5454")
    private String senderAccountUri;

    @Schema(example = "CREDIT")
    private String fundingSource;

    private String transferReference;

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public Money getTransferAmount() { return transferAmount; }
    public void setTransferAmount(Money transferAmount) { this.transferAmount = transferAmount; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public Participant getSender() { return sender; }
    public void setSender(Participant sender) { this.sender = sender; }
    public Participant getRecipient() { return recipient; }
    public void setRecipient(Participant recipient) { this.recipient = recipient; }
    public String getReceivingAccountUri() { return receivingAccountUri; }
    public void setReceivingAccountUri(String receivingAccountUri) { this.receivingAccountUri = receivingAccountUri; }
    public String getSenderAccountUri() { return senderAccountUri; }
    public void setSenderAccountUri(String senderAccountUri) { this.senderAccountUri = senderAccountUri; }
    public String getFundingSource() { return fundingSource; }
    public void setFundingSource(String fundingSource) { this.fundingSource = fundingSource; }
    public String getTransferReference() { return transferReference; }
    public void setTransferReference(String transferReference) { this.transferReference = transferReference; }
}

