package com.sudarshan.core.events;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class ProductReservationCancelledEvent {
	
	private final String productId;
	
	private final int quantity;
	
	private final String orderId;
	
	private final String userId;

	private final String message;
}
