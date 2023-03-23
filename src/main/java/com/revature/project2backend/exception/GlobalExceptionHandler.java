package com.revature.project2backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

}
