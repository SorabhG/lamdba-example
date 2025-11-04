package com.example.lambda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ProductHandlerSingleTest {

    private Function<Map<String, Object>, Object> handleProduct;

    @BeforeEach
    void setup() {
        ProductHandlerSingle handler = new ProductHandlerSingle();
        handleProduct = handler.handleProduct();
    }

    @Test
    void testAddProduct() throws Exception {
        Map<String, Object> event = new HashMap<>();
        Map<String, Object> requestContext = new HashMap<>();
        Map<String, Object> http = new HashMap<>();
        http.put("method", "POST");
        requestContext.put("http", http);
        event.put("requestContext", requestContext);

        Map<String, Object> body = new HashMap<>();
        body.put("name", "Laptop");
        body.put("price", 1500);
        event.put("body", new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body));

        Object result = handleProduct.apply(event);

        assertNotNull(result);
        ProductHandlerSingle.Product product = (ProductHandlerSingle.Product) result; // cast to Product
        assertEquals("Laptop", product.getName());
        assertEquals(1500.0, product.getPrice());
        assertNotNull(product.getId());
    }

    @Test
    void testGetProducts() throws Exception {
        // First, add a product
        testAddProduct();

        Map<String, Object> event = new HashMap<>();
        Map<String, Object> requestContext = new HashMap<>();
        Map<String, Object> http = new HashMap<>();
        http.put("method", "GET");
        requestContext.put("http", http);
        event.put("requestContext", requestContext);

        Object result = handleProduct.apply(event);
        assertNotNull(result);
        assertTrue(result instanceof List);

        List<ProductHandlerSingle.Product> products = (List<ProductHandlerSingle.Product>) result;
        assertFalse(products.isEmpty());
        assertEquals("Laptop", products.get(0).getName());
    }
}
