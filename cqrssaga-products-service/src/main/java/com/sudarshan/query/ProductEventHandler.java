package com.sudarshan.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sudarshan.core.data.ProductEntity;
import com.sudarshan.core.data.ProductRepository;
import com.sudarshan.core.events.ProductCreatedEvent;
import com.sudarshan.core.events.ProductReservationCancelledEvent;
import com.sudarshan.core.events.ProductReservedEvents;

import lombok.extern.slf4j.Slf4j;

//EventHandlers are also known as Projections
@Component
@ProcessingGroup("createProduct") //This will help the eventhandler not to fetch the same messages again, logically group all the eventhandlers
@Slf4j
public class ProductEventHandler {
	
	private ProductRepository productRepository;
	
	public ProductEventHandler(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	//This will handle exception from event handler in this class, also if we want to propagate the exception to publisher and also rollback all other
	//event handler exception we need to throw it and provide ListenerInvocationErrorHandler
	@ExceptionHandler(resultType = Exception.class)
	public void handleException(Exception exception) throws Exception {
		//log error message
		throw exception;
	}
	
	//This will handle exception from event handler in this class
	@ExceptionHandler(resultType = IllegalStateException.class)
	public void handleIllegalStateException(IllegalStateException illegalStateException) {
		//log error message
	}
	
	//Once an @EventSourcingHandler has finished executing, the event is published to the EventBus, which is then caught by the @EventHandlers.
	@EventHandler
	public void on(ProductCreatedEvent productCreatedEvent) throws Exception {
		ProductEntity productEntity = new ProductEntity();
		
		BeanUtils.copyProperties(productCreatedEvent, productEntity);
		
		productRepository.save(productEntity);
		
		/*if (true) { // even though we throw it here after save, the database will be rollbacked and it would sent to @Exceptionhandler from their it is
			//send to custom ListenerInvocationErrorHandler and then to controlleradvice of controller due to which it will rollback all the transactions in all other eventhandlers.
				throw new Exception("Intentionally throw in Event handler");
		}*/		

	}
	
	@EventHandler
	public void on(ProductReservedEvents productReservedEvents) throws Exception {
		ProductEntity productEntity = productRepository.findByProductId(productReservedEvents.getProductId());
		
		productEntity.setQuantity(productEntity.getQuantity() - productReservedEvents.getQuantity());
		
		productRepository.save(productEntity);
		log.info("Product Reserved event called in Product Service for orderId :: {} and productId :: {}", productReservedEvents.getOrderId(), productReservedEvents.getProductId());
	}
	
	@EventHandler
	public void on(ProductReservationCancelledEvent productReservationCancelledEvent) throws Exception {
		ProductEntity productEntity = productRepository.findByProductId(productReservationCancelledEvent.getProductId());
		
		productEntity.setQuantity(productEntity.getQuantity() + productReservationCancelledEvent.getQuantity());
		
		productRepository.save(productEntity);
		log.info("Product Reserved event rollbacked in Product Service for orderId :: {} and productId :: {}", productReservationCancelledEvent.getOrderId(), productReservationCancelledEvent.getProductId());
	}
	
	@ResetHandler  //This method is called when reset of token is initialized for replay of events
	public void reset() {
		productRepository.deleteAll();
	}

}
