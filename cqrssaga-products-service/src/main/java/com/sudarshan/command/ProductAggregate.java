package com.sudarshan.command;

import java.math.BigDecimal;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.sudarshan.core.commands.CancelProductReservationCommand;
import com.sudarshan.core.commands.ReserveProductCommand;
import com.sudarshan.core.events.ProductCreatedEvent;
import com.sudarshan.core.events.ProductReservationCancelledEvent;
import com.sudarshan.core.events.ProductReservedEvents;

@Aggregate(snapshotTriggerDefinition = "eventCountProductSnapshotDefination")
public class ProductAggregate {

	@AggregateIdentifier
	private String productId;

	private String title;

	private BigDecimal price;

	private Integer quantity;

	public ProductAggregate() {

	}
	
	//Here we can perform validation
	@CommandHandler
	public ProductAggregate(CreateProductCommand createProductCommand) throws Exception {
		if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <=0) {
			throw new IllegalArgumentException("Price cannot be less than or equal to zero");
		}

		if(createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()) {
			throw new IllegalArgumentException("Title cannot be blank");
		}

		ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
		BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

		//This will dispatch the event to all the event handlers and also is available to update the state
		AggregateLifecycle.apply(productCreatedEvent);
		
		/*if (true) { // even though we throw it here after apply, the event is not published to the event store, also the exception is wrapped into 
					//commandExecutionException and will be sent to controlleradvice
			throw new Exception("Intentionally throw in command handler");
		}*/
	}
	
	@CommandHandler
	public void handle(ReserveProductCommand reserveProductCommand) throws Exception {
		if(quantity < reserveProductCommand.getQuantity()) {
			throw new IllegalArgumentException("Insufficient number of items in stock");
		}
		
		ProductReservedEvents productReservedEvents = ProductReservedEvents.builder()
																.orderId(reserveProductCommand.getOrderId())
																.productId(reserveProductCommand.getProductId())
																.quantity(reserveProductCommand.getQuantity())
																.userId(reserveProductCommand.getUserId())
																.build();
		
		AggregateLifecycle.apply(productReservedEvents);
	}
	
	//Here we are setting the current state of aggregate with event
	@EventSourcingHandler
	public void on(ProductCreatedEvent productCreatedEvent) {
		this.productId = productCreatedEvent.getProductId();
		this.price = productCreatedEvent.getPrice();
		this.quantity = productCreatedEvent.getQuantity();
		this.title = productCreatedEvent.getTitle();
	}
	
	@EventSourcingHandler
	public void on(ProductReservedEvents productReservedEvents) {
		this.quantity -= productReservedEvents.getQuantity();
	}
	
	@CommandHandler
	public void handle(CancelProductReservationCommand cancelProductReservationCommand) throws Exception {
		
		ProductReservationCancelledEvent productReservationCancelledEvent = ProductReservationCancelledEvent.builder()
																.orderId(cancelProductReservationCommand.getOrderId())
																.productId(cancelProductReservationCommand.getProductId())
																.quantity(cancelProductReservationCommand.getQuantity())
																.userId(cancelProductReservationCommand.getUserId())
																.message(cancelProductReservationCommand.getMessage())
																.build();
		
		AggregateLifecycle.apply(productReservationCancelledEvent);
	}
	

	@EventSourcingHandler
	public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
		this.quantity += productReservationCancelledEvent.getQuantity();
	}

}
