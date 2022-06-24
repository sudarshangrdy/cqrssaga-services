package com.sudarshan.saga;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import com.sudarshan.command.commands.ApproveOrderCommand;
import com.sudarshan.command.commands.RejectOrderCommand;
import com.sudarshan.command.events.OrderApprovedEvent;
import com.sudarshan.command.events.OrderCreatedEvent;
import com.sudarshan.command.events.OrderRejectEvent;
import com.sudarshan.command.model.OrderSummary;
import com.sudarshan.core.commands.CancelProductReservationCommand;
import com.sudarshan.core.commands.ProcessPaymentCommand;
import com.sudarshan.core.commands.ReserveProductCommand;
import com.sudarshan.core.events.PaymentProcessedEvent;
import com.sudarshan.core.events.ProductReservationCancelledEvent;
import com.sudarshan.core.events.ProductReservedEvents;
import com.sudarshan.core.model.User;
import com.sudarshan.core.query.FetchUserPaymentDetailsQuery;
import com.sudarshan.query.FindOrderQuery;

import lombok.extern.slf4j.Slf4j;

@Saga
@Slf4j
public class OrderSaga {
	
	@Autowired //Saga gets serialized, so we are adding transient to avoid serilization of commandgateway
	private transient CommandGateway commandGateway;
	
	@Autowired
	private transient QueryGateway queryGateway;
	
	@Autowired
	private transient DeadlineManager deadlineManager;
	
	@Autowired
	private transient QueryUpdateEmitter queryUpdateEmitter;

	private String scheduleId;
	
	@StartSaga //provided to the first eventhandler, every saga class should have start saga and end saga annotation
	@SagaEventHandler(associationProperty = "orderId") //Association property should be property name in eventobject, 
	public void handle(OrderCreatedEvent orderCreatedEvent) {
		ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
																			.orderId(orderCreatedEvent.getOrderId())
																			.productId(orderCreatedEvent.getProductId())
																			.quantity(orderCreatedEvent.getQuantity())
																			.userId(orderCreatedEvent.getUserId())
																			.build();
		
		log.info("OrderCreated event handled for orderId :: {} and productId :: {}", orderCreatedEvent.getOrderId(), orderCreatedEvent.getProductId());
		commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
					CommandResultMessage<? extends Object> commandResultMessage) {
				if(commandResultMessage.isExceptional()) {
					RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderCreatedEvent.getOrderId(), commandResultMessage.exceptionResult().getLocalizedMessage());
					
					commandGateway.send(rejectOrderCommand);
				}
			}
			
		});
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservedEvents productReservedEvents) {
		log.info("Product Reserved event called in Order Service for orderId :: {} and productId :: {}", productReservedEvents.getOrderId(), productReservedEvents.getProductId());
		User user = null;
		try {
			FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery();
			fetchUserPaymentDetailsQuery.setUserId(productReservedEvents.getUserId());
			
			user = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
		} catch (Exception exception) {
			log.error(exception.getLocalizedMessage());
			cancelProductReservation(productReservedEvents, exception.getLocalizedMessage());
			return;
		}
		
		if(user == null) {
			log.info("Cannot fetch user payements details for user ");
			cancelProductReservation(productReservedEvents, "User Payment details not available");
			return;
		}
		log.info("Succesffuly fetched user payements details for user :: {}", user.getUserId());
		
		//Scheduling the deadline, this gets scheduled immediately, so if the event is successful, we need to close the deadline before the time scheduled
		scheduleId = deadlineManager.schedule(Duration.of(10, ChronoUnit.MINUTES), "payment-processing-deadline", productReservedEvents);
		
		ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
																			.orderId(productReservedEvents.getOrderId())
																			.paymentId(UUID.randomUUID().toString())
																			.paymentDetails(user.getPaymentDetails())
																			.build();
		
		//For testing deadline
		/*if(true)
			return;*/
		
		String result = null;
		try {
			result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
			
		} catch(Exception exception) {
			log.error(exception.getLocalizedMessage());
			cancelProductReservation(productReservedEvents, exception.getLocalizedMessage());
			return;
		}
		
		if(result == null) {
			log.info("Not able to process payment for the user :: {}", user.getUserId());
			cancelProductReservation(productReservedEvents, "Not able to process payment for the user");
			return;
		}
		
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessedEvent paymentProcessedEvent) {
		//When the proccessing is completed, then we need to cancel the deadline corresponding to the event.
		//deadlineManager.cancelAll("payment-processing-deadline");
		cancelDeadLine();
		ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
		
		commandGateway.send(approveOrderCommand);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent orderApprovedEvent) {
		log.info("Order Approved successfullt for orderid :: {}", orderApprovedEvent.getOrderId());
		//SagaLifecycle.end();
		queryUpdateEmitter.emit(FindOrderQuery.class, query -> true, new OrderSummary(orderApprovedEvent.getOrderId(), orderApprovedEvent.getOrderStatus(), ""));
	}
	
	private void cancelProductReservation(ProductReservedEvents productReservedEvents, String errorMessage) {
		cancelDeadLine();
		CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
																										 .orderId(productReservedEvents.getOrderId())
																										 .productId(productReservedEvents.getProductId())
																										 .quantity(productReservedEvents.getQuantity())
																										 .userId(productReservedEvents.getUserId())
																										 .message(errorMessage)
																										 .build();
		
		commandGateway.send(cancelProductReservationCommand);
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
		RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCancelledEvent.getOrderId(), productReservationCancelledEvent.getMessage());
		
		commandGateway.send(rejectOrderCommand);
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderRejectEvent orderRejectEvent) {
		log.info("Order rollbacked successfully.");
		queryUpdateEmitter.emit(FindOrderQuery.class, query -> true, new OrderSummary(orderRejectEvent.getOrderId(), orderRejectEvent.getOrderStatus(), orderRejectEvent.getMessage()));
	}
	
	//When a deadline is invoked after 10 seconds, ten deadline handler would be called, where we can call compensating steps or call any other steps
	@DeadlineHandler(deadlineName = "payment-processing-deadline")
	public void handleDeadLine(ProductReservedEvents productReservedEvents) {
		log.info("Did not receive any input, so calling cancellationevent");
		cancelProductReservation(productReservedEvents, "deadline called");
	}
	
	private void cancelDeadLine() {
		//Here we are cancelling particular deadline with id
		if(scheduleId!=null) {
			deadlineManager.cancelSchedule("payment-processing-deadline", scheduleId);
			scheduleId=null;
		}
		
	}
}
