package com.example.mastercardsend.client;

public class SendApiException extends RuntimeException {
    private final int status;
    private final String body;
    private String code;
    private String messageDetail;

    public SendApiException(String message) {
        super(message);
        this.status = 0;
        this.body = null;
    }

    public SendApiException(String message, Throwable cause) {
        super(message, cause);
        this.status = 0;
        this.body = null;
    }

    protected SendApiException(int status, String body) {
        super("Send API error: " + status);
        this.status = status;
        this.body = body;
        parse(body);
    }

    public static SendApiException from(int status, String body) {
        return new SendApiException(status, body);
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public String getCode() { return code; }
    public String getMessageDetail() { return messageDetail; }

    private void parse(String json) {
        if (json == null || json.isBlank()) return;
        try {
            com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
            if (node.has("Errors")) {
                com.fasterxml.jackson.databind.JsonNode errors = node.get("Errors");
                if (errors.isArray() && errors.size() > 0) {
                    com.fasterxml.jackson.databind.JsonNode e = errors.get(0);
                    if (e.has("ReasonCode")) this.code = e.get("ReasonCode").asText();
                    if (e.has("Description")) this.messageDetail = e.get("Description").asText();
                }
            } else if (node.has("errorCode")) {
                this.code = node.get("errorCode").asText();
                if (node.has("errorDescription")) this.messageDetail = node.get("errorDescription").asText();
            }
        } catch (Exception ignored) {
        }
    }
}

