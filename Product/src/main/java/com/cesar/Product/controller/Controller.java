package com.cesar.Product.controller;

import com.cesar.Product.persistence.entity.ProductEntity;
import com.cesar.Product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class Controller{

	private final ProductService productService;

	public Controller(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<?> fetchAll() {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(productService.findAll());
	}
}