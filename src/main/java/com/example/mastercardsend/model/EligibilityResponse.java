package com.example.mastercardsend.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class EligibilityResponse extends Extensible {
    @Schema(example = "true")
    private Boolean eligible;

    public Boolean getEligible() { return eligible; }
    public void setEligible(Boolean eligible) { this.eligible = eligible; }
}

