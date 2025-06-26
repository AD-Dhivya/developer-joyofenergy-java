package uk.tw.energy.exception;

import java.time.Instant;

public class ErrorResponse {

    private String message;
    private String error;
    private Instant timestamp;
    private int status;
    private Object details;

    public ErrorResponse(String message, String error, int status) {
        this(message, error, status, null);
    }

    public ErrorResponse(String message, String error, int status, Object details) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.timestamp = Instant.now();
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public Object getDetails() {
        return details;
    }
}
