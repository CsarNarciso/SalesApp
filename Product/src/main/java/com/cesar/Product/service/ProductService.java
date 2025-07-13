package com.cesar.Product.service;

import com.cesar.Product.persistence.entity.ProductEntity;
import com.cesar.Product.persistence.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

	public List<ProductEntity> findAll() {
        return productRepo.findAll();
    }
}