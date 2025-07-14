package com.cesar.Authentication.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({NoAuthenticatedException.class, JWTVerificationException.class})
    public ResponseEntity<?> handleNoAuthenticated(Exception ex){
        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
    }
}