package com.sudarshan.rest.query.model;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductRestModel {
	
	private String productId;
	
	private String title;
	
	private BigDecimal price;
	
	private Integer quantity;

}
