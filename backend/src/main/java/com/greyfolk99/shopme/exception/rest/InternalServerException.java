package com.greyfolk99.shopme.exception.rest;

import com.greyfolk99.shopme.exception.ExceptionClass;
import org.springframework.http.HttpStatus;

/*
    * This exception is thrown when an internal error occurs.
    * default reference class: GENERAL
    * default code: 500 (Internal Server Error)
 */
public class InternalServerException extends RestControllerException {

    private final static HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalServerException(ExceptionClass exceptionClass, String message){
        super(exceptionClass, DEFAULT_HTTP_STATUS, message);
    }

    public InternalServerException(ExceptionClass exceptionClass, HttpStatus httpStatus, String message) {
        super(exceptionClass, httpStatus, message);
    }
}
