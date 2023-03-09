package com.greyfolk99.shopme.exception.rest;

import com.greyfolk99.shopme.exception.ExceptionClass;
import org.springframework.http.HttpStatus;


public abstract class RestControllerException extends RuntimeException {

    private final static ExceptionClass DEFAULT_EXCEPTION_CLASS = ExceptionClass.GENERAL;
    private final ExceptionClass exceptionClass;
    private final HttpStatus httpStatus;

    public RestControllerException(HttpStatus httpStatus, String message){
        super(message);
        this.exceptionClass = DEFAULT_EXCEPTION_CLASS;
        this.httpStatus = httpStatus;
    }

    public RestControllerException(ExceptionClass exceptionClass, HttpStatus httpStatus, String message){
        super(message);
        this.exceptionClass = exceptionClass;
        this.httpStatus = httpStatus;
    }

    public ExceptionClass getExceptionClass(){ return exceptionClass; }
    public int getHttpStatusCode() { return httpStatus.value(); }
    public String getHttpStatusType(){ return httpStatus.getReasonPhrase(); }
    public HttpStatus getHttpStatus() { return httpStatus; }
}
