package com.example.mastercardsend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TransferStatusResponse")
public class TransferStatusResponse {
    @Schema(example = "t123")
    private String transferId;
    @Schema(example = "COMPLETED")
    private String status;

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static TransferStatusResponse fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, TransferStatusResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse response", e);
        }
    }
}

