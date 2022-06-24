package com.sudarshan.command.model;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateOrderCommand {
	
	@TargetAggregateIdentifier
	public final String orderId;
	
	private final String userId;
	
	private final String productId;
	
	private final int quantity;
	
	private final String addressId;
	
	private final OrderStatus orderStatus;

}
