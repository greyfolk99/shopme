package com.greyfolk99.shopme.exception.rest;

import com.greyfolk99.shopme.exception.ExceptionClass;
import org.springframework.http.HttpStatus;

/*
    * This exception is thrown when an internal error occurs.
    * default reference class: GENERAL
    * default code: 400 (Bad Request)
 */
public class ValidationFailedException extends RestControllerException {

    private final static HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public ValidationFailedException(ExceptionClass exceptionClass, String message){
        super(exceptionClass, DEFAULT_HTTP_STATUS, message);
    }

    public ValidationFailedException(ExceptionClass exceptionClass, HttpStatus httpStatus, String message) {
        super(exceptionClass, httpStatus, message);
    }
}
