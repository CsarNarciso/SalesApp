package com.cesar.Authentication.persistence.dto;

public record LogInRequest(
	String username,
	String password
){}