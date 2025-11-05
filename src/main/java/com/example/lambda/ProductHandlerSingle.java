package com.example.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Component
public class ProductHandlerSingle {

    private static final Map<Long, Product> productRepo = new LinkedHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);
    @Bean
    public Function<Map<String, Object>, Object> handleProduct() {
        return input -> {
            System.out.println("Singh is King: " + input);
            // Try top-level httpMethod first
            String method = (String) input.get("httpMethod");

            // Fallback: AWS Lambda proxy integration style
            if (method == null && input.get("requestContext") instanceof Map rc) {
                Map<String, Object> http = (Map<String, Object>) rc.get("http");
                if (http != null) {
                    method = (String) http.get("method");
                }
            }

            if ("POST".equalsIgnoreCase(method)) {
                Map<String, Object> body = parseBody(input.get("body"));
                Product product = new Product();
                product.setName((String) body.get("name"));
                product.setPrice(Double.valueOf(body.get("price").toString()));

                long id = idCounter.getAndIncrement();
                product.setId(id);
                productRepo.put(id, product);
                return product;

            } else if ("GET".equalsIgnoreCase(method)) {
                return new ArrayList<>(productRepo.values());
            } else {
                return Collections.singletonMap("message", "Unsupported method");
            }
        };
    }

    // Utility to parse JSON body string
    private Map<String, Object> parseBody(Object body) {
        if (body instanceof String s) {
            try {
                return new ObjectMapper().readValue(s, Map.class);
            } catch (Exception e) {
                throw new RuntimeException("Invalid JSON body", e);
            }
        } else if (body instanceof Map m) {
            return m;
        } else {
            return Map.of();
        }
    }

    public void clearProducts() {
        productRepo.clear(); // wherever your static map is
    }

    public static class Product {
        private Long id;
        private String name;
        private Double price;
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }
}
