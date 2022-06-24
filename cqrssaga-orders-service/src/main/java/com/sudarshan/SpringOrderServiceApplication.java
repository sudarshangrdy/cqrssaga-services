package com.sudarshan;

import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringOrderServiceApplication.class, args);
	}
	
	@Bean
	public DeadlineManager deadLineManager(Configuration configuration, SpringTransactionManager transactionManager) {
		return SimpleDeadlineManager.builder()
									.scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
									.transactionManager(transactionManager)
									.build();
	}

}