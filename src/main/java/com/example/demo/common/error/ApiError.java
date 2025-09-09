package com.example.demo.common.error;

import java.time.Instant;

public class ApiError {
    private final Instant timestamp = Instant.now();
    private final String error;
    private final String message;
    private final String path;

    public ApiError(String error, String message, String path) {
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() { return timestamp; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
}

