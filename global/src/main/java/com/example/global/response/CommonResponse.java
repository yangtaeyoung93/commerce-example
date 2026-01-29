package com.example.global.response;

import com.example.global.response.error.ErrorResponse;

public class CommonResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final ErrorResponse error;

    public CommonResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.error = null;
    }

    public CommonResponse(boolean success, ErrorResponse error) {
        this.success = success;
        this.data = null;
        this.message = null;
        this.error = error;
    }

    public static <T> CommonResponse<T> success(T data, String message) {
        return new CommonResponse<>(true, data, message);
    }

    public static CommonResponse<?> error(ErrorResponse error) {
        return new CommonResponse<>(false, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public ErrorResponse getError() {
        return error;
    }
}
