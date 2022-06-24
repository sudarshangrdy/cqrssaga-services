package com.sudarshan.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RejectOrderCommand {
	
	@TargetAggregateIdentifier
	private String orderId;
	
	private String message;

}
