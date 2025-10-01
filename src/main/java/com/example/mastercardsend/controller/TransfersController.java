package com.example.mastercardsend.controller;

import com.example.mastercardsend.client.SendApiClient;
import com.example.mastercardsend.model.TransferCreateRequest;
import com.example.mastercardsend.model.TransferCreateResponse;
import com.example.mastercardsend.model.TransferStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfers")
public class TransfersController {

    private final SendApiClient client;

    public TransfersController(SendApiClient client) {
        this.client = client;
    }

    @PostMapping
    @Operation(summary = "Create transfer")
    @ApiResponse(responseCode = "200", description = "Created",
        content = @Content(schema = @Schema(implementation = TransferCreateResponse.class)))
    public ResponseEntity<TransferCreateResponse> create(@RequestHeader(value = "Idempotency-Key", required = false) String idem,
                                                         @Valid @RequestBody TransferCreateRequest request) {
        return ResponseEntity.ok(client.createTransfer(request, idem));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transfer status")
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(schema = @Schema(implementation = TransferStatusResponse.class)))
    public ResponseEntity<TransferStatusResponse> get(@PathVariable("id") String id) {
        return ResponseEntity.ok(client.getTransferStatus(id));
    }
}

