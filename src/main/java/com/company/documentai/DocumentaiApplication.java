package com.company.documentai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//TODO - denna ska inte behövas
@SpringBootApplication(scanBasePackages = "com.company.documentai")
public class DocumentaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentaiApplication.class, args);
	}

}