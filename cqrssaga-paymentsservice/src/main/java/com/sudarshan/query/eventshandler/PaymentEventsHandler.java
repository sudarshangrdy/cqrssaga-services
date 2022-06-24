package com.sudarshan.query.eventshandler;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.sudarshan.core.events.PaymentProcessedEvent;
import com.sudarshan.query.data.PaymentEntity;
import com.sudarshan.query.data.PaymentsRepository;

@Component
@ProcessingGroup("createProduct")
public class PaymentEventsHandler {
	
	private final PaymentsRepository paymentsRepository;
	
	public PaymentEventsHandler(PaymentsRepository paymentsRepository) {
		this.paymentsRepository = paymentsRepository;
	}
	
	@EventHandler
	public void on(PaymentProcessedEvent paymentProcessedEvent) {
		PaymentEntity paymentEntity = new PaymentEntity(paymentProcessedEvent.getPaymentId(), paymentProcessedEvent.getOrderId());
		
		paymentsRepository.save(paymentEntity);
	}
}
