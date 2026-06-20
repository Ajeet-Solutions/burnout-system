package com.bdos.burnout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Yahan humne default security ko band (exclude) kar diya hai
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BurnoutSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BurnoutSystemApplication.class, args);
	}
}