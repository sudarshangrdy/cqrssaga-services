package com.sudarshan.command.handler;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.sudarshan.command.commands.ApproveOrderCommand;
import com.sudarshan.command.commands.RejectOrderCommand;
import com.sudarshan.command.events.OrderApprovedEvent;
import com.sudarshan.command.events.OrderCreatedEvent;
import com.sudarshan.command.events.OrderRejectEvent;
import com.sudarshan.command.model.CreateOrderCommand;
import com.sudarshan.command.model.OrderStatus;

@Aggregate
public class OrderAggregate {
	
	@AggregateIdentifier
	private String orderId;
	private String productId;
	private String userId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;
	
	public OrderAggregate() {
		
	}
	
	@CommandHandler
	public OrderAggregate(CreateOrderCommand createOrderCommand) {
		
		OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
		
		BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
		
		AggregateLifecycle.apply(orderCreatedEvent);
	}
	
	@EventSourcingHandler
	public void on(OrderCreatedEvent orderCreatedEvent) {
		this.orderId = orderCreatedEvent.getOrderId();
		this.productId = orderCreatedEvent.getProductId();
		this.userId = orderCreatedEvent.getUserId();
		this.quantity = orderCreatedEvent.getQuantity();
		this.addressId = orderCreatedEvent.getAddressId();
		this.orderStatus = orderCreatedEvent.getOrderStatus();
	}
	
	@CommandHandler
	public void on(ApproveOrderCommand approveOrderCommand) {
		OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approveOrderCommand.getOrderId());
		
		AggregateLifecycle.apply(orderApprovedEvent);
	}
	
	@EventSourcingHandler
	public void on(OrderApprovedEvent orderApprovedEvent) {
		this.orderStatus = orderApprovedEvent.getOrderStatus();
	}
	
	@CommandHandler
	public void on(RejectOrderCommand rejectOrderCommand) {
		OrderRejectEvent orderRejectEvent = new OrderRejectEvent(rejectOrderCommand.getOrderId(), rejectOrderCommand.getMessage());
		
		AggregateLifecycle.apply(orderRejectEvent);
	}
	
	@EventSourcingHandler
	public void on(OrderRejectEvent orderRejectEvent) {
		this.orderStatus = orderRejectEvent.getOrderStatus();
	}

}
