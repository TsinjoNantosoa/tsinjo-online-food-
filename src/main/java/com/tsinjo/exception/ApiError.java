package com.tsinjo.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(Instant timestamp, int status, String error, String message,
                       Map<String, String> fieldErrors, String path) {
    public ApiError(Instant timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, null, path);
    }
}
