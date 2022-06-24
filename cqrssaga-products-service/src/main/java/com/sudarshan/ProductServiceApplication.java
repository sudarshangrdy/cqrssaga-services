package com.sudarshan;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.sudarshan.command.interceptor.CreateProductCommandInterceptor;
import com.sudarshan.core.errorhandling.ProductsServiceEventErrorHandler;

import reactor.core.scheduler.Schedulers.Snapshot;

@SpringBootApplication
@EnableEurekaClient //Works with only eureka client jars present in class path
//@EnableDiscoveryClient //works with any client like consul, eureka or kubernetes which are present in the classpath
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
	
	@Autowired 
	public void registerCreateProductCommandInterceptor(ApplicationContext 	applicationContext, CommandBus commandBus) {
		commandBus.registerDispatchInterceptor(applicationContext.getBean(
				CreateProductCommandInterceptor.class)); 
	}
	
	@Autowired
	public void configure(EventProcessingConfigurer eventProcessingConfigurer) {
		eventProcessingConfigurer.registerListenerInvocationErrorHandler("createProduct", config -> new ProductsServiceEventErrorHandler());
		
		//We can also create this way instead of ListenerInvocationErrorHandler
		//eventProcessingConfigurer.registerListenerInvocationErrorHandler("createProduct", config -> PropagatingErrorHandler.instance());
	}
	
	@Bean(name="eventCountProductSnapshotDefination")
	public SnapshotTriggerDefinition productSnapSHotTriggerDefination(Snapshotter snapshotter) {
		return new EventCountSnapshotTriggerDefinition(snapshotter, 3);
	}

}
