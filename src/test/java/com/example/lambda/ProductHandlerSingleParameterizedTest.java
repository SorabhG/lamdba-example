package com.example.lambda;

import com.example.lambda.ProductHandlerSingle;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProductHandlerSingleParameterizedTest {
    private static final ProductHandlerSingle handler = new ProductHandlerSingle();
    private static final Function<Map<String, Object>, Object> handleProduct = handler.handleProduct();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Provides events in order:
     * - POST Laptop
     * - POST Mouse
     * - GET (should return 2 products)
     * - Unsupported method
     * - AWS-style POST (Keyboard)
     */
    static Stream<Map<String, Object>> eventProvider() throws Exception {
        Map<String, Object> postLaptop = new HashMap<>();
        postLaptop.put("httpMethod", "POST");
        postLaptop.put("body", mapper.writeValueAsString(Map.of("name", "Laptop", "price", 1500)));

        Map<String, Object> postMouse = new HashMap<>();
        postMouse.put("httpMethod", "POST");
        postMouse.put("body", mapper.writeValueAsString(Map.of("name", "Mouse", "price", 50)));

        Map<String, Object> getEvent = new HashMap<>();
        getEvent.put("httpMethod", "GET");

        Map<String, Object> unsupported = new HashMap<>();
        unsupported.put("httpMethod", "DELETE");

        Map<String, Object> awsPost = new HashMap<>();
        Map<String, Object> requestContext = new HashMap<>();
        Map<String, Object> http = new HashMap<>();
        http.put("method", "POST");
        requestContext.put("http", http);
        awsPost.put("requestContext", requestContext);
        awsPost.put("body", mapper.writeValueAsString(Map.of("name", "Keyboard", "price", 80)));

        return Stream.of(postLaptop, postMouse, getEvent, unsupported, awsPost);
    }

    @ParameterizedTest
    @MethodSource("eventProvider")
    void testHandleProduct(Map<String, Object> event) throws Exception {
        Object result = handleProduct.apply(event);

        if (result instanceof ProductHandlerSingle.Product product) {
            // POST results
            assertNotNull(product.getId(), "Product ID should not be null");
            assertNotNull(product.getName(), "Product name should not be null");
            assertTrue(product.getPrice() > 0, "Product price should be positive");
        } else if (result instanceof List<?> list) {
            // GET result
            assertFalse(list.isEmpty(), "GET list should not be empty");
            // Check that all elements are Product
            for (Object o : list) {
                assertTrue(o instanceof ProductHandlerSingle.Product, "List item should be Product");
            }
            // Optional: check the size matches the expected number of products
            assertEquals(2, list.size(), "GET should return 2 products after 2 POSTs");
        } else if (result instanceof Map<?, ?> map) {
            // Unsupported method
            assertEquals("Unsupported method", map.get("message"));
        } else {
            fail("Unexpected return type: " + (result == null ? "null" : result.getClass()));
        }
    }
}

