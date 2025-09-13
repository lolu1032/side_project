package com.example.sideProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = "com.example")
@EnableScheduling
public class SideProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideProjectApplication.class, args);
	}

}
