package com.bdos.burnout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

// Exclude default security configuration and enable asynchronous processing for emails
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableAsync
public class BurnoutSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BurnoutSystemApplication.class, args);
	}
}