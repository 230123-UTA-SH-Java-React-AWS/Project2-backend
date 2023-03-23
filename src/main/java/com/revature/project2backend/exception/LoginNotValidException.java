package com.revature.project2backend.exception;

public class LoginNotValidException extends RuntimeException{

    public LoginNotValidException(String message) {
        super(message);
    }
}
