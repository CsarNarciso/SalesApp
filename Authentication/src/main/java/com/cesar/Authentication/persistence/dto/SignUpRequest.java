package com.cesar.Authentication.persistence.dto;

public record SignUpRequest(
	String username,
	String password
){}