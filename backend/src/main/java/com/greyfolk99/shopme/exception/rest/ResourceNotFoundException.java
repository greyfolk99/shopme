package com.greyfolk99.shopme.exception.rest;

import com.greyfolk99.shopme.exception.ExceptionClass;
import org.springframework.http.HttpStatus;

/*
    * This exception is thrown when a resource already exists in the database.
    * default reference class: GENERAL
    * default code: 404 (Not Found)
 */
public class ResourceNotFoundException extends RestControllerException {

    private final static HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND;

    public ResourceNotFoundException(String message){
        super(DEFAULT_HTTP_STATUS, message);
    }

    public ResourceNotFoundException(HttpStatus httpStatus, String message){
        super(httpStatus, message);
    }

    public ResourceNotFoundException(ExceptionClass exceptionClass, String message){
        super(exceptionClass, DEFAULT_HTTP_STATUS, message);
    }

    public ResourceNotFoundException(ExceptionClass exceptionClass, HttpStatus httpStatus, String message){
        super(exceptionClass, httpStatus, message);
    }
}
