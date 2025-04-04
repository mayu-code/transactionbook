package com.transaction.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class BookApplication {

	public static void main(String[] args) {	
		SpringApplication.run(BookApplication.class, args);
	}

}
