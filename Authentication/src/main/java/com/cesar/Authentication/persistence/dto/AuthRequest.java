package com.cesar.Authentication.persistence.dto;

public record AuthRequest(
	String username,
	String password
){}