package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class TransferDetailsResponse extends Extensible {
    @Schema(example = "t123")
    private String transferId;
    @Schema(example = "COMPLETED")
    private String status;

    public String getTransferId() { return transferId; }
    public void setTransferId(String transferId) { this.transferId = transferId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

