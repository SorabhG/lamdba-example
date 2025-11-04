package com.example.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class LocalTest {
    public static void main(String[] args) throws Exception {
        ProductHandlerSingle handler = new ProductHandlerSingle();
        ObjectMapper mapper = new ObjectMapper();

        // Mock POST event
        Map<String,Object> postEvent = new HashMap<>();
        postEvent.put("httpMethod", "POST");
        postEvent.put("body", "{\"name\":\"Laptop\",\"price\":1500}");

        // invoke the handler
        Object result = handler.handleProduct().apply(postEvent);
        System.out.println("POST result: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));

        // Mock GET event
        Map<String,Object> getEvent = new HashMap<>();
        getEvent.put("httpMethod", "GET");

        Object getResult = handler.handleProduct().apply(getEvent);
        System.out.println("GET result: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getResult));
    }
}

