package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class EligibilityRequest extends Extensible {

    @Schema(example = "+14155550123")
    @NotBlank
    private String recipientPhone;

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
}

