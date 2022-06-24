package com.sudarshan.rest.query.controllers;

import java.util.List;

import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudarshan.rest.query.FindProductsQuery;
import com.sudarshan.rest.query.model.ProductRestModel;

@RestController
@RequestMapping("/products")
public class ProductsQueryController {
	
	private final QueryGateway queryGateway;
	
	public ProductsQueryController(QueryGateway queryGateway) {
		this.queryGateway = queryGateway;
	}
	
	@GetMapping
	public List<ProductRestModel> getProducts() {
		
		FindProductsQuery findProductsQuery = new FindProductsQuery();
		
		List<ProductRestModel> productsResModelList = queryGateway.query(findProductsQuery, ResponseTypes.multipleInstancesOf(ProductRestModel.class)).join();
	
		return productsResModelList;
	}

}
