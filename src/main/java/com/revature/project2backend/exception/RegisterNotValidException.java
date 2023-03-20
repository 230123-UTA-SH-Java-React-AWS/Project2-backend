package com.revature.project2backend.exception;

public class RegisterNotValidException extends RuntimeException{
    public RegisterNotValidException(String message) {
        super(message);
    }
}
