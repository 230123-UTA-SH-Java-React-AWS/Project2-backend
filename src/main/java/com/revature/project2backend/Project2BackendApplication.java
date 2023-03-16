package com.revature.project2backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Project2BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Project2BackendApplication.class, args);
	}
}
