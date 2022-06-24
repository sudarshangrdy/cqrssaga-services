package com.sudarshan.command.events;

import com.sudarshan.command.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
	
	private String orderId;
	private String productId;
	private String userId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;

}
