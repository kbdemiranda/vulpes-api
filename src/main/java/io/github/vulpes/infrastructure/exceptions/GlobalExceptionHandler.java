package io.github.vulpes.infrastructure.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {VulpesException.class})
    public ResponseEntity<Object> handleSuaExcecaoPersonalizada(VulpesException vulpesException) {
        return new ResponseEntity<>(vulpesException.getMessage(), new HttpHeaders(), vulpesException.getCode());
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleGenericException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), VulpesException.INTERNAL_SERVER_ERROR);
    }

}
