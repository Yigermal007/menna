package com.example.mastercardsend.client;

import com.example.mastercardsend.model.TransferCreateRequest;
import com.example.mastercardsend.model.TransferCreateResponse;
import com.example.mastercardsend.model.TransferStatusResponse;
import com.example.mastercardsend.model.QuoteRequest;
import com.example.mastercardsend.model.QuoteResponse;
import com.example.mastercardsend.model.EligibilityRequest;
import com.example.mastercardsend.model.EligibilityResponse;

public interface SendApiClient {

    TransferCreateResponse createTransfer(TransferCreateRequest request);

    TransferCreateResponse createTransfer(TransferCreateRequest request, String idempotencyKey);

    TransferStatusResponse getTransferStatus(String transferId);

    QuoteResponse getQuote(QuoteRequest request);

    EligibilityResponse checkEligibility(EligibilityRequest request);
}

