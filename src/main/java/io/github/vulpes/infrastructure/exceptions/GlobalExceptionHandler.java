package io.github.vulpes.infrastructure.exceptions;

import io.github.vulpes.infrastructure.http.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {VulpesException.class})
    public ResponseEntity<Object> handleSuaExcecaoPersonalizada(VulpesException vulpesException, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(vulpesException.getCode())
                .error("VulpesException")
                .message(vulpesException.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(body, new HttpHeaders(), vulpesException.getCode());
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(VulpesException.INTERNAL_SERVER_ERROR)
                .error("Internal Server Error")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(body, new HttpHeaders(), VulpesException.INTERNAL_SERVER_ERROR);
    }

}
