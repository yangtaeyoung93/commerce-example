package com.example.global.exception;

import com.example.global.response.CommonResponse;
import com.example.global.response.error.ErrorCode;
import com.example.global.response.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CommonResponse> handleBadRequestException(BadRequestException e){
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_INPUT, e.getDetails());
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus())
                .body(CommonResponse.error(errorResponse));
    }

    @ExceptionHandler(NotFoundResourceException.class)
    public ResponseEntity<CommonResponse> handleNotFoundException(NotFoundResourceException e){
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.RESOURCE_NOT_FOUND, null);
        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
                .body(CommonResponse.error(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleException(Exception e){
        e.printStackTrace();
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_ERROR,null);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(CommonResponse.error(errorResponse));
    }
}
