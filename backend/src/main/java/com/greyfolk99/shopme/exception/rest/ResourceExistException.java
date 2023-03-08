package com.greyfolk99.shopme.exception.rest;

import com.greyfolk99.shopme.exception.ExceptionClass;
import org.springframework.http.HttpStatus;

/*
    * This exception is thrown when a resource already exists in the database.
    * default reference class: GENERAL
    * default code: 409 (Conflict)
 */
public class ResourceExistException extends RestControllerException {

    private final static HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.CONFLICT;

    public ResourceExistException(ExceptionClass exceptionClass, String message){
        super(exceptionClass, DEFAULT_HTTP_STATUS, message);
    }

    public ResourceExistException(ExceptionClass exceptionClass, HttpStatus httpStatus, String message){
        super(exceptionClass, httpStatus, message);
    }
}
