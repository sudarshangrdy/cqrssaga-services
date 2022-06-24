package com.sudarshan.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.sudarshan.core.commands.ProcessPaymentCommand;
import com.sudarshan.core.events.PaymentProcessedEvent;

@Aggregate
public class PaymentAggregate {
	
	private String orderId;
	
	@AggregateIdentifier
	private String paymentId;
	
	@CommandHandler
	public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) throws IllegalArgumentException{
		if(processPaymentCommand.getOrderId() == null || processPaymentCommand.getOrderId().isBlank()) {
			throw new IllegalArgumentException("OrderID is not valid");
		}
		
		if(processPaymentCommand.getPaymentId() == null || processPaymentCommand.getPaymentId().isBlank()) {
			throw new IllegalArgumentException("PaymentId is not valid");
		}
		
		PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
																			.orderId(processPaymentCommand.getOrderId())
																			.paymentId(processPaymentCommand.getPaymentId())
																			.build();
		
		AggregateLifecycle.apply(paymentProcessedEvent);
	}
	
	@EventSourcingHandler
	public void on(PaymentProcessedEvent paymentProcessedEvent) {
		this.orderId = paymentProcessedEvent.getOrderId();
		this.paymentId = paymentProcessedEvent.getPaymentId();
	}

}
