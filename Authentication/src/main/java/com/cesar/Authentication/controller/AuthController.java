package com.cesar.Authentication.controller;

import com.cesar.Authentication.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cesar.Authentication.persistence.dto.LogInRequest;
import com.cesar.Authentication.persistence.dto.SignUpRequest;

@RestController
@RequestMapping("/auth")
public class AuthController{

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}


	@PostMapping("/register")
	public ResponseEntity<?> signup(@RequestBody SignUpRequest signupRequest) {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(authService.signup(signupRequest));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LogInRequest loginRequest, HttpServletResponse res){
		authService.login(loginRequest, res);
		return ResponseEntity.status(HttpStatus.OK).body("Successfully authenticated!");
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(HttpServletRequest req, HttpServletResponse res) {
		authService.refresh(req, res);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Tokens successfully refreshed!");
	}
}