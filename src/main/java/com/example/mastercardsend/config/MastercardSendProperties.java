package com.example.mastercardsend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mastercard.send")
public class MastercardSendProperties {

    private String baseUrl;
    private String consumerKey;
    private String keyFile;
    private String keyAlias;
    private String keyPassword;
    private boolean signingDisabled;
    private String partnerId;
    private String acceptLanguage;
    private String userAgent = "mastercard-send-service/1.0";
    private String clientReferenceId;

    private Mtls mtls = new Mtls();
    private ApiPaths apiPaths = new ApiPaths();
    private Webhook webhook = new Webhook();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public boolean isSigningDisabled() {
        return signingDisabled;
    }

    public void setSigningDisabled(boolean signingDisabled) {
        this.signingDisabled = signingDisabled;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getAcceptLanguage() { return acceptLanguage; }
    public void setAcceptLanguage(String acceptLanguage) { this.acceptLanguage = acceptLanguage; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getClientReferenceId() { return clientReferenceId; }
    public void setClientReferenceId(String clientReferenceId) { this.clientReferenceId = clientReferenceId; }

    public Mtls getMtls() {
        return mtls;
    }

    public void setMtls(Mtls mtls) {
        this.mtls = mtls;
    }

    public ApiPaths getApiPaths() {
        return apiPaths;
    }

    public void setApiPaths(ApiPaths apiPaths) {
        this.apiPaths = apiPaths;
    }

    public Webhook getWebhook() { return webhook; }
    public void setWebhook(Webhook webhook) { this.webhook = webhook; }

    public static class Mtls {
        private boolean enabled;
        private String keyStore;
        private String keyStorePassword;
        private String trustStore;
        private String trustStorePassword;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getKeyStore() {
            return keyStore;
        }

        public void setKeyStore(String keyStore) {
            this.keyStore = keyStore;
        }

        public String getKeyStorePassword() {
            return keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getTrustStore() {
            return trustStore;
        }

        public void setTrustStore(String trustStore) {
            this.trustStore = trustStore;
        }

        public String getTrustStorePassword() {
            return trustStorePassword;
        }

        public void setTrustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
        }
    }

    public static class ApiPaths {
        private String createTransfer = "/send/v1/partners/{partnerId}/transactions";
        private String transferStatus = "/send/v1/partners/{partnerId}/transactions/{id}";
        private String quote = "/send/v1/partners/{partnerId}/quotes";
        private String eligibility = "/send/v1/partners/{partnerId}/eligibility";
        private String cancelTransfer = "/send/v1/partners/{partnerId}/transactions/{id}/cancel";
        private String reverseTransfer = "/send/v1/partners/{partnerId}/transactions/{id}/reverse";
        private String transferDetails = "/send/v1/partners/{partnerId}/transactions/{id}/details";

        public String getCreateTransfer() {
            return createTransfer;
        }

        public void setCreateTransfer(String createTransfer) {
            this.createTransfer = createTransfer;
        }

        public String getTransferStatus() {
            return transferStatus;
        }

        public void setTransferStatus(String transferStatus) {
            this.transferStatus = transferStatus;
        }

        public String getQuote() { return quote; }
        public void setQuote(String quote) { this.quote = quote; }

        public String getEligibility() { return eligibility; }
        public void setEligibility(String eligibility) { this.eligibility = eligibility; }

        public String getCancelTransfer() { return cancelTransfer; }
        public void setCancelTransfer(String cancelTransfer) { this.cancelTransfer = cancelTransfer; }

        public String getReverseTransfer() { return reverseTransfer; }
        public void setReverseTransfer(String reverseTransfer) { this.reverseTransfer = reverseTransfer; }

        public String getTransferDetails() { return transferDetails; }
        public void setTransferDetails(String transferDetails) { this.transferDetails = transferDetails; }
    }

    public static class Webhook {
        private boolean enabled;
        private String secret;
        private String signatureHeader = "X-Signature";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public String getSignatureHeader() { return signatureHeader; }
        public void setSignatureHeader(String signatureHeader) { this.signatureHeader = signatureHeader; }
    }
}

