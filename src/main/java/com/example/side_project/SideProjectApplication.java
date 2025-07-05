package com.example.side_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableScheduling
public class SideProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideProjectApplication.class, args);
	}

}
