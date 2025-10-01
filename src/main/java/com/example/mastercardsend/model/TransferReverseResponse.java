package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class TransferReverseResponse extends Extensible {
    @Schema(example = "REVERSED")
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

