package com.sudarshan.command.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequestModel {
	
	@NotBlank
	private String productId;
	
	@Min(value = 1)
	private int quantity;
	
	@NotBlank
	private String addressId;

}
