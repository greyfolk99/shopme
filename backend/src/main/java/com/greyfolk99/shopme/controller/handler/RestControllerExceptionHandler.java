package com.greyfolk99.shopme.controller.handler;

import com.greyfolk99.shopme.exception.rest.RestControllerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/*
    This class is used to handle exceptions thrown by controllers with @ResponseBody.

    response example:

        body: {
           "error type": "Bad Request",
           "code": "400",
           "message": "error message"
        }
 */
@RestControllerAdvice @Slf4j
public class RestControllerExceptionHandler {

    @ExceptionHandler(value = RestControllerException.class)
    public ResponseEntity<?> handleCustomException(RestControllerException e, HttpServletRequest request) {

        log.error("[handCustomException] {}, {}, {}", request.getRequestURI(), e.getMessage(), e.getExceptionClass().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json", StandardCharsets.UTF_8));
        Map<String, String> body = new HashMap<>();
        body.put("error type", e.getHttpStatusType());
        body.put("code", Integer.toString(e.getHttpStatusCode()));
        body.put("message", e.getMessage());

        return new ResponseEntity<>(body,headers,e.getHttpStatus());
    }
}