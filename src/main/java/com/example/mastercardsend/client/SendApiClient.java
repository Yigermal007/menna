package com.example.mastercardsend.client;

import com.example.mastercardsend.model.TransferCreateRequest;
import com.example.mastercardsend.model.TransferCreateResponse;
import com.example.mastercardsend.model.TransferStatusResponse;
import com.example.mastercardsend.model.QuoteRequest;
import com.example.mastercardsend.model.QuoteResponse;
import com.example.mastercardsend.model.EligibilityRequest;
import com.example.mastercardsend.model.EligibilityResponse;
import com.example.mastercardsend.model.TransferCancelResponse;
import com.example.mastercardsend.model.TransferReverseResponse;
import com.example.mastercardsend.model.TransferDetailsResponse;
import com.example.mastercardsend.model.PaymentTransferRequest;

public interface SendApiClient {

    TransferCreateResponse createTransfer(TransferCreateRequest request);

    TransferCreateResponse createTransfer(TransferCreateRequest request, String idempotencyKey);
    TransferCreateResponse createTransfer(PaymentTransferRequest request);
    TransferCreateResponse createTransfer(PaymentTransferRequest request, String idempotencyKey);

    TransferStatusResponse getTransferStatus(String transferId);

    QuoteResponse getQuote(QuoteRequest request);

    EligibilityResponse checkEligibility(EligibilityRequest request);

    TransferCancelResponse cancelTransfer(String transferId);

    TransferReverseResponse reverseTransfer(String transferId);

    TransferDetailsResponse getTransferDetails(String transferId);
}

