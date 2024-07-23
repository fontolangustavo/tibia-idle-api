package com.fontolan.tibiaidle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TibiaidleApplication {

	public static void main(String[] args) {
		SpringApplication.run(TibiaidleApplication.class, args);
	}

}
