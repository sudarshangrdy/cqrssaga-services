package com.sudarshan.command;

import java.math.BigDecimal;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@RequiredArgsConstructor
public class CreateProductCommand {
	
	@TargetAggregateIdentifier
	private final String productId;
	
	private final String title;
	
	private final BigDecimal price;
	
	private final Integer quantity;

}
