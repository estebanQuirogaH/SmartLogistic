package com.projectStore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.projectStore")
public class SmartLogisticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartLogisticsApplication.class, args);
	}

}