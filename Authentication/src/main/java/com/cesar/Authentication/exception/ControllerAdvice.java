package com.cesar.Authentication.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler()
    public ResponseEntity<String> handleNoAuthenticated(NoAuthenticatedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<String> handleJWTVerification(JWTVerificationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}