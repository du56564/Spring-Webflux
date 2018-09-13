package com.bofa.microservices.repository;

import com.bofa.microservices.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository
        extends ReactiveMongoRepository<Product, String> {
}