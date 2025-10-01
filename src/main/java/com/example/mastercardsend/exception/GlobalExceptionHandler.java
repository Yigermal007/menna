package com.example.mastercardsend.exception;

import com.example.mastercardsend.client.SendApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SendApiException.class)
    public ResponseEntity<Map<String, Object>> handleSend(SendApiException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "SEND_API_ERROR");
        body.put("status", ex.getStatus());
        body.put("details", ex.getBody());
        if (ex.getCode() != null) body.put("code", ex.getCode());
        if (ex.getMessageDetail() != null) body.put("message", ex.getMessageDetail());
        int status = ex.getStatus() > 0 ? ex.getStatus() : 502;
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "VALIDATION_ERROR");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
        log.error("Unhandled error", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INTERNAL_SERVER_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

