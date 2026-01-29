package com.example.global.response.error;

import java.util.Map;

public class ErrorResponse {
    private final String code;
    private final String message;
    private final Map<String, Object> details;

    public ErrorResponse(ErrorCode error, Map<String, Object> details) {
        this.code = error.getCode();
        this.message = error.getMessage();
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
