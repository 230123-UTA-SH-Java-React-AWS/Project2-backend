package com.revature.project2backend.exception;

public class EmailConfirmationException extends RuntimeException {
    public EmailConfirmationException(String message) {
        super(message);
    }
}