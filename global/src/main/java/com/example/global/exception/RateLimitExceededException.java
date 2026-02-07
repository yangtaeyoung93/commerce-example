package com.example.global.exception;

import com.example.global.response.error.ErrorCode;

import java.util.LinkedHashMap;
import java.util.Map;

public class RateLimitExceededException extends RuntimeException{

    private final ErrorCode errorCode;
    private final Map<String,Object> details;

    public RateLimitExceededException(ErrorCode errorCode,String message) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = Map.of("message",message);
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }
    public Map<String, Object> getDetails() {
        return details;
    }

}
