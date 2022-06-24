package com.sudarshan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer //Here when we have multiple eureka server then we need to have eureka client as well
public class SpringDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDiscoveryApplication.class, args);
	}

}