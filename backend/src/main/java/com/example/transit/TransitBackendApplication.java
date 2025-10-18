package com.example.transit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TransitBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransitBackendApplication.class, args);
	}

}
