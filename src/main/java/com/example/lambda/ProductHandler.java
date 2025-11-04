package com.example.lambda;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;


@Component
public class ProductHandler {

    private static final Map<Long, Product> productRepo = new LinkedHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);

    // Supplier = GET /products (returns list)
    @Bean
    public Supplier<List<Product>> getProducts() {
        return () -> new ArrayList<>(productRepo.values());
    }

    // Function = POST /product (adds one product)
    @Bean
    public Function<Product, Product> addProduct() {
        return product -> {
            long id = idCounter.getAndIncrement();
            product.setId(id);
            productRepo.put(id, product);
            return product;
        };
    }

    // Simple model class
    public static class Product {
        private Long id;
        private String name;
        private Double price;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }
}
