package com.example.mastercardsend.controller;

import com.example.mastercardsend.client.SendApiClient;
import com.example.mastercardsend.model.EligibilityRequest;
import com.example.mastercardsend.model.EligibilityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eligibility")
@Tag(name = "Eligibility")
public class EligibilityController {

    private final SendApiClient client;

    public EligibilityController(SendApiClient client) { this.client = client; }

    @PostMapping
    @Operation(summary = "Check eligibility")
    public ResponseEntity<EligibilityResponse> check(@Valid @RequestBody EligibilityRequest request) {
        return ResponseEntity.ok(client.checkEligibility(request));
    }
}

