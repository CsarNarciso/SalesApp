package com.cesar.Product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}
	
	@CommandLineRunner
	void run(ProductRepository repo) {
		// Pre load test products
		List<ProductEntity> products = List.of(
			ProductEntity.builder().name("Laptop").price(100.0).build(),
			ProductEntity.builder().name("Mountain bike").price(200.0).build(),
			ProductEntity.builder().name("Camping backpak").price(34.0).build(),
		);
		repo.saveAll(products);
	}
}