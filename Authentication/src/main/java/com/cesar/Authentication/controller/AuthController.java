package com.cesar.Authentication.controller;

import com.cesar.Authentication.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cesar.Authentication.persistence.dto.AuthRequest;

@RestController
@RequestMapping("/auth")
public class AuthController{

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}


	@PostMapping("/register")
	public ResponseEntity<?> signup(@RequestBody AuthRequest signupRequest) {
		authService.signup(signupRequest);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("User registered successfully!");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest, HttpServletResponse res){
		authService.login(loginRequest, res);
		return ResponseEntity.status(HttpStatus.OK).body("Successfully authenticated!");
	}

	@GetMapping("/check")
	public ResponseEntity<?> check() {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(HttpServletRequest req, HttpServletResponse res) {
		authService.refresh(req, res);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Tokens successfully refreshed!");
	}
}