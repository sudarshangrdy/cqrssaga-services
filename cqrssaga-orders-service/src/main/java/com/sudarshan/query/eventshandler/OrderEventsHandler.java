package com.sudarshan.query.eventshandler;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sudarshan.command.events.OrderApprovedEvent;
import com.sudarshan.command.events.OrderCreatedEvent;
import com.sudarshan.command.events.OrderRejectEvent;
import com.sudarshan.query.data.OrderEntity;
import com.sudarshan.query.data.OrdersRepository;

@Component
@ProcessingGroup("createorder")
public class OrderEventsHandler {
	
	private final OrdersRepository ordersRepository;
	
	public OrderEventsHandler(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
	}
	
	@EventHandler
	public void on(OrderCreatedEvent orderCreatedEvent) {
		OrderEntity orderEntity = new OrderEntity();
		BeanUtils.copyProperties(orderCreatedEvent, orderEntity);
		
		ordersRepository.save(orderEntity);
	}
	
	@EventHandler
	public void on(OrderApprovedEvent orderApprovedEvent) {
		OrderEntity orderEntity = ordersRepository.findByOrderId(orderApprovedEvent.getOrderId());
		
		if(orderEntity!=null) {
			orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
			ordersRepository.save(orderEntity);
		}
		
	}
	
	@EventHandler
	public void on(OrderRejectEvent orderRejectEvent) {
		OrderEntity orderEntity = ordersRepository.findByOrderId(orderRejectEvent.getOrderId());
		
		if(orderEntity!=null) {
			orderEntity.setOrderStatus(orderRejectEvent.getOrderStatus());
			ordersRepository.save(orderEntity);
		}
		
	}

}
