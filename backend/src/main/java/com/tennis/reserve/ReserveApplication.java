package com.tennis.reserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ReserveApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReserveApplication.class, args);
	}

}

