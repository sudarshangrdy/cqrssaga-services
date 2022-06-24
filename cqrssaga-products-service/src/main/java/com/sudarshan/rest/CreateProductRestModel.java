package com.sudarshan.rest;

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CreateProductRestModel {
	
	@NotBlank(message = "Title is required")
	@Size(min = 2, max = 15, message = "The size should be minimum of 2 and max of 15")
	private String title;
	
	@Min(value = 1, message = "Price should be greater than 1")
	private BigDecimal price;
	
	@Min(value = 1, message = "Quantity should be greater than 1")
	@Max(value = 5, message = "Quantity should not be greater than 5")
	private Integer quantity;
}
