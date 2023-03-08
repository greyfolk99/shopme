package com.greyfolk99.shopme.exception.rest;

import com.greyfolk99.shopme.exception.ExceptionClass;
import org.springframework.http.HttpStatus;

/*
    * This exception is thrown when a product is out of stock.
    * default reference class: ITEM
    * default code: 409 (Conflict)
 */
public class OutOfStockException extends RestControllerException {

    private final static ExceptionClass DEFAULT_EXCEPTION_CLASS = ExceptionClass.ITEM;

    private final static HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.CONFLICT;

    public OutOfStockException(String message){
        super(DEFAULT_EXCEPTION_CLASS, DEFAULT_HTTP_STATUS, message);
    }

    public OutOfStockException(HttpStatus httpStatus, String message){
        super(DEFAULT_EXCEPTION_CLASS, httpStatus, message);
    }
}
