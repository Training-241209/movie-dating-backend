package com.moviedating.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;


@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
	// 	Dotenv dotenv = Dotenv.configure()
        //         .directory("backend")
        //         .load();
        // dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(),
        //         entry.getValue()));

		SpringApplication.run(BackendApplication.class, args);
	}

}
