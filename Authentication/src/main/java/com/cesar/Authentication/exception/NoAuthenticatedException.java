package com.cesar.Authentication.exception;

public class NoAuthenticatedException extends RuntimeException {
    public NoAuthenticatedException(String message){
        super(message);
    }
}