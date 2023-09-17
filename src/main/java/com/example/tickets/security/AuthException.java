package com.example.tickets.security;

public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }
}
