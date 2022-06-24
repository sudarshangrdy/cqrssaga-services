package com.sudarshan.query.queryhandler;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.sudarshan.command.model.OrderSummary;
import com.sudarshan.query.FindOrderQuery;
import com.sudarshan.query.data.OrderEntity;
import com.sudarshan.query.data.OrdersRepository;

@Component
public class OrderQueryHandler {
	
	private final OrdersRepository ordersRepository;
	
	public OrderQueryHandler(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
	}
	
	@QueryHandler
	public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
		OrderEntity orderEntity	= ordersRepository.findByOrderId(findOrderQuery.getOrderId());
		
		OrderSummary orderSummary = new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
		
		return orderSummary;
	}
	
	
	
}
