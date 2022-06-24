package com.sudarshan.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.sudarshan.core.model.PaymentDetails;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class ProcessPaymentCommand {
	
	@TargetAggregateIdentifier
	private final String paymentId;
	
	private final String orderId;
	
	private final PaymentDetails paymentDetails;

}
