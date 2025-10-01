package com.example.mastercardsend.client;

public class RetryableSendApiException extends SendApiException {
    public RetryableSendApiException(int status, String body) {
        super(status, body);
    }

    public RetryableSendApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

