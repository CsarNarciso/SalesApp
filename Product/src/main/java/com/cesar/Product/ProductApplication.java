package com.cesar.Product;

import com.cesar.Product.persistence.entity.ProductEntity;
import com.cesar.Product.persistence.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ProductApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner preloadData(ProductRepository repo) {
		return args -> {
			// Preload test products
			List<ProductEntity> products = List.of(
					ProductEntity.builder().name("Laptop").price(100.0F).build(),
					ProductEntity.builder().name("Mountain bike").price(200.0F).build(),
					ProductEntity.builder().name("Camping backpack").price(34.0F).build()
			);
			repo.saveAll(products);
		};
	}
}