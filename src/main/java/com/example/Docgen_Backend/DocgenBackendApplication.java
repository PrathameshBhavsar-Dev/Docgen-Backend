package com.example.Docgen_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DocgenBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocgenBackendApplication.class, args);
	}

}