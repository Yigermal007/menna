package com.example.mastercardsend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/callbacks/mastercard")
@Tag(name = "Callbacks")
public class CallbacksController {

    private static final Logger log = LoggerFactory.getLogger(CallbacksController.class);

    @PostMapping
    @Operation(summary = "Receive Mastercard Send notifications")
    public ResponseEntity<Void> receive(@RequestBody String payload, HttpHeaders headers) {
        // Verify HMAC signature if configured
        try {
            boolean ok = verify(headers, payload);
            if (!ok) {
                return ResponseEntity.status(401).build();
            }
        } catch (Exception e) {
            log.warn("Callback signature verification failed", e);
            return ResponseEntity.status(401).build();
        }
        log.info("Received callback: {}", payload);
        return ResponseEntity.ok().build();
    }

    private boolean verify(HttpHeaders headers, String payload) throws Exception {
        com.example.mastercardsend.config.MastercardSendProperties props = this.props;
        if (props == null || !props.getWebhook().isEnabled()) return true;
        String headerName = props.getWebhook().getSignatureHeader();
        String sig = headers.getFirst(headerName);
        if (sig == null) return false;
        String secret = props.getWebhook().getSecret();
        if (secret == null || secret.isBlank()) return false;
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] computed = mac.doFinal(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String computedB64 = java.util.Base64.getEncoder().encodeToString(computed);
        return java.security.MessageDigest.isEqual(computedB64.getBytes(), sig.getBytes());
    }

    private final com.example.mastercardsend.config.MastercardSendProperties props;

    public CallbacksController(com.example.mastercardsend.config.MastercardSendProperties props) {
        this.props = props;
    }
}

