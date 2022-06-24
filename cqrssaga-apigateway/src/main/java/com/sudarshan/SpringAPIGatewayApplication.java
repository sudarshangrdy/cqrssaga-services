package com.sudarshan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//Gateway works with spring web flux and uses netty as server
@SpringBootApplication
@EnableDiscoveryClient
public class SpringAPIGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAPIGatewayApplication.class, args);
	}

}
