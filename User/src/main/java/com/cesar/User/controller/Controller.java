package com.cesar.User.controller;

import com.cesar.User.persistence.entity.UserEntity;
import com.cesar.User.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class Controller{

	private final UserService userService;

	public Controller(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody UserEntity user) {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(userService.create(user));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> fetchById(@PathVariable Long id) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(userService.findById(id));
	}
	
	@GetMapping
	public ResponseEntity<?> fetchAll() {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(userService.findAll());
	}
}