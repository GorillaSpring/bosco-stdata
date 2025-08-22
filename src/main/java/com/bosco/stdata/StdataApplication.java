package com.bosco.stdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StdataApplication {

	public static void main(String[] args) {
		SpringApplication.run(StdataApplication.class, args);
	}

}
