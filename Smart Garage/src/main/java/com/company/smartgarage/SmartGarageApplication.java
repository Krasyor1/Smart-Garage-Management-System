package com.company.smartgarage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class SmartGarageApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartGarageApplication.class, args);
	}

}