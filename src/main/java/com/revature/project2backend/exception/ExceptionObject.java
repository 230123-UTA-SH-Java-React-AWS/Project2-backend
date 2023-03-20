package com.revature.project2backend.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ExceptionObject {
    private String httpStatus;
    private String message;
    private Date timestamp;
}
