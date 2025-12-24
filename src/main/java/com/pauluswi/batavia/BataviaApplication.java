package com.pauluswi.batavia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class BataviaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BataviaApplication.class, args);
	}

}
