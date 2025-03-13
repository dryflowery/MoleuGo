package com.Moleugo.moleugo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MoleugoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoleugoApplication.class, args);
	}

}
