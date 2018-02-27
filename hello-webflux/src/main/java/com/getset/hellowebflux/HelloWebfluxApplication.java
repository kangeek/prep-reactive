package com.getset.hellowebflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class HelloWebfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloWebfluxApplication.class, args);
	}
}
