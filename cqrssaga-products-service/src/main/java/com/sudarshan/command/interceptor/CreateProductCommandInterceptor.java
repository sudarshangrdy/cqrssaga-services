package com.sudarshan.command.interceptor;

import java.util.List;
import java.util.function.BiFunction;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import com.sudarshan.command.CreateProductCommand;
import com.sudarshan.core.data.ProductLookupEntity;
import com.sudarshan.core.data.ProductLookupRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>>{
	
	private final ProductLookupRepository productLookupRepository;
	
	public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
		this.productLookupRepository = productLookupRepository;
	}

	@Override
	public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
			List<? extends CommandMessage<?>> messages) {
		return (index, command)-> {
			log.info("Interceptor command: "+command.getPayloadType());
			if(CreateProductCommand.class.equals(command.getPayloadType())) {
				/*if(((CreateProductCommand)command).getPrice().compareTo(BigDecimal.ZERO) <=0) {
					throw new IllegalArgumentException("Price cannot be less than or equal to zero");
				}

				if(((CreateProductCommand)command).getTitle() == null || ((CreateProductCommand)command).getTitle().isBlank()) {
					throw new IllegalArgumentException("Title cannot be blank");
				}*/
				
				CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
				ProductLookupEntity productLookupEntity = productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
				if(productLookupEntity != null) {
					throw new IllegalStateException(String.format("Product with id %s and title %s is already available", createProductCommand.getProductId(), createProductCommand.getTitle()));
				}
			}
			return command;
		};
	}
	
	
}
