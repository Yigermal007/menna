package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class TransferCancelResponse extends Extensible {
    @Schema(example = "CANCELLED")
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

