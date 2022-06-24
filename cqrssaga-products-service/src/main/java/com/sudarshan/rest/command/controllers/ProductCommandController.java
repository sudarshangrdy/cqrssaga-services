package com.sudarshan.rest.command.controllers;


import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudarshan.command.CreateProductCommand;
import com.sudarshan.rest.CreateProductRestModel;

@RestController
@RequestMapping("/products")
public class ProductCommandController {
	
	final Environment env;
	
	final CommandGateway commandGateway;
	
	@Autowired //Classes with single constructor does not require @AUtowired, only when we use multiple constructor we need it.
	public ProductCommandController(Environment env, CommandGateway commandGateway) {
		this.env = env;
		this.commandGateway = commandGateway;
	}
	
	//Here an update is an command, now when we receive an update request, then we need to publish to the command bus.
	@PostMapping()
	public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel) {
		CreateProductCommand createProductCommand = CreateProductCommand.builder()
																		.price(createProductRestModel.getPrice())
																		.quantity(createProductRestModel.getQuantity())
																		.title(createProductRestModel.getTitle())
																		.productId(UUID.randomUUID().toString())
																		.build();
		
		String returnValue = null;
		/*try {//Commented as handled in Controller advice
			returnValue = commandGateway.sendAndWait(createProductCommand);
		} catch(Exception exception) {
			returnValue = exception.getLocalizedMessage();
		}*/
		returnValue = commandGateway.sendAndWait(createProductCommand);
		return "HTTP Post Handled "+returnValue;
	}  
	
	//Here server port will be referred as local.server.port when dynamic port is provided
	/*@GetMapping()
	public String getProduct() {
		return "HTTP Get Handled "+ env.getProperty("local.server.port");
	}*/
	
	@PutMapping()
	public String updateProduct() {
		return "HTTP Put Handled";
	}
	
	@DeleteMapping()
	public String deleteProduct() {
		return "HTTP Delete Handled";
	}
}
