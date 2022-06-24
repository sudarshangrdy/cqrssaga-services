package com.sudarshan.query;

import lombok.Value;

//This is a query class, to fetch the data
@Value
public class FindOrderQuery {
	
	private final String orderId; 
}
