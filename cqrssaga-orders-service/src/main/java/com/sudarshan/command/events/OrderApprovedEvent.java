package com.sudarshan.command.events;

import com.sudarshan.command.model.OrderStatus;

import lombok.Value;

@Value
public class OrderApprovedEvent {
	
	private String orderId;
	
	private final OrderStatus orderStatus = OrderStatus.APPROVED;

}
