package com.example.tickets.controller.exception;


import com.example.tickets.security.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> serverExceptionHandler(Exception ex) {
        if (ex instanceof AccessDeniedException | ex instanceof AuthException) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error:" + ex.getMessage()));
        }
        return Mono.just(ResponseEntity.badRequest().body("Error:" + ex.getMessage()));
    }
}
