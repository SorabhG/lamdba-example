package com.example.lambda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SpringBootApplication
public class SpringLambdaProductsApplication {

	public static void main(String[] args) {
		var ctx = SpringApplication.run(SpringLambdaProductsApplication.class, args);
		// Lookup the function bean and cast to the correct type
		@SuppressWarnings("unchecked")
		Function<Map<String, Object>, Object> func =
				(Function<Map<String, Object>, Object>) ctx.getBean(FunctionCatalog.class)
						.lookup("handleProduct");

		Map<String,Object> event = new HashMap<>();
		event.put("httpMethod", "POST");
		event.put("body", "{\"name\":\"Laptop\",\"price\":1500}");

		System.out.println(func.apply(event));
	}

	@Bean
	public Function<String, String> uppercase() {
		return value -> value.toUpperCase();
	}

}
