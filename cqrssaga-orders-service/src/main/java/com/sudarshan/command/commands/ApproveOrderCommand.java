package com.sudarshan.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApproveOrderCommand {
	
	@TargetAggregateIdentifier
	private String orderId;
}
