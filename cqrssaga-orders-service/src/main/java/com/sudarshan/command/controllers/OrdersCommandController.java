package com.sudarshan.command.controllers;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudarshan.command.model.CreateOrderCommand;
import com.sudarshan.command.model.CreateOrderRequestModel;
import com.sudarshan.command.model.OrderStatus;
import com.sudarshan.command.model.OrderSummary;
import com.sudarshan.query.FindOrderQuery;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {
	
	private final CommandGateway commandGateway;
	
	private final QueryGateway queryGateway;
	
	public OrdersCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
		this.commandGateway = commandGateway;
		this.queryGateway = queryGateway;
	}
	
	@PostMapping
	//public String createOrder(@Valid @RequestBody CreateOrderRequestModel createOrderRequestModel) { //used without subscription
	public OrderSummary createOrder(@Valid @RequestBody CreateOrderRequestModel createOrderRequestModel) {
		
		String orderId = UUID.randomUUID().toString();
		
		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
																  .orderId(orderId)
																  .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
																  .productId(createOrderRequestModel.getProductId())
																  .quantity(createOrderRequestModel.getQuantity())
																  .addressId(createOrderRequestModel.getAddressId())
																  .orderStatus(OrderStatus.CREATED)
																  .build();
		
		FindOrderQuery findOrderQuery = new FindOrderQuery(orderId);
		
		//Once this is registered in Order saga we need to emit the OrderSummery Records so that updates are sent here regularly using QueryUpdateEmitter
		SubscriptionQueryResult<OrderSummary, OrderSummary> subscriptioResult = queryGateway.subscriptionQuery(findOrderQuery, ResponseTypes.instanceOf(OrderSummary.class), ResponseTypes.instanceOf(OrderSummary.class));
		
		try {
			commandGateway.sendAndWait(createOrderCommand);
			return subscriptioResult.updates().blockFirst();
		} finally {
			subscriptioResult.close();
		}
		
		//Used without subscription query returning orderid
		/*String returnValue = commandGateway.sendAndWait(createOrderCommand);
		
		return returnValue;*/ 
	}

}
