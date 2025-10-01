package com.example.mastercardsend.controller;

import com.example.mastercardsend.client.SendApiClient;
import com.example.mastercardsend.model.QuoteRequest;
import com.example.mastercardsend.model.QuoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotes")
@Tag(name = "Quotes")
public class QuotesController {

    private final SendApiClient client;

    public QuotesController(SendApiClient client) { this.client = client; }

    @PostMapping
    @Operation(summary = "Get quote")
    public ResponseEntity<QuoteResponse> getQuote(@Valid @RequestBody QuoteRequest request) {
        return ResponseEntity.ok(client.getQuote(request));
    }
}

