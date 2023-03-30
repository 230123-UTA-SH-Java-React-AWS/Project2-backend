package com.revature.project2backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginNotValidException.class)
    public ResponseEntity<ExceptionObject> handleLoginNotValidException(LoginNotValidException ex){
        ExceptionObject exceptionObject = new ExceptionObject();
        exceptionObject.setHttpStatus(HttpStatus.UNAUTHORIZED.toString());
        exceptionObject.setMessage(ex.getMessage());
        exceptionObject.setTimestamp(new Date());

        return new ResponseEntity<>(exceptionObject, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RegisterNotValidException.class)
    public ResponseEntity<ExceptionObject> handleRegisterNotValidException(RegisterNotValidException ex){
        ExceptionObject exceptionObject = new ExceptionObject();
        exceptionObject.setHttpStatus(HttpStatus.CONFLICT.toString());
        exceptionObject.setMessage(ex.getMessage());
        exceptionObject.setTimestamp(new Date());

        return new ResponseEntity<>(exceptionObject, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailConfirmationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleEmailConfirmationException(EmailConfirmationException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

}

/*
* GlobalExceptionHandler Class Documentation
This class serves as a global exception handler for the application, handling specific exceptions and returning appropriate HTTP responses with custom error messages.

Annotations
@ControllerAdvice: Indicates that this class is an advice that should be applied to all controllers in the application. This allows for centralized exception handling across the application.
Methods
handleLoginNotValidException(LoginNotValidException ex)
This method handles the LoginNotValidException and returns a custom ResponseEntity<ExceptionObject> containing the error details. It sets the HTTP status to HttpStatus.UNAUTHORIZED.

Parameters:

LoginNotValidException ex: The exception instance that triggered this method.
Returns:

ResponseEntity<ExceptionObject>: A ResponseEntity containing an ExceptionObject with the error details.
handleRegisterNotValidException(RegisterNotValidException ex)
This method handles the RegisterNotValidException and returns a custom ResponseEntity<ExceptionObject> containing the error details. It sets the HTTP status to HttpStatus.CONFLICT.

Parameters:

RegisterNotValidException ex: The exception instance that triggered this method.
Returns:

ResponseEntity<ExceptionObject>: A ResponseEntity containing an ExceptionObject with the error details.
* */