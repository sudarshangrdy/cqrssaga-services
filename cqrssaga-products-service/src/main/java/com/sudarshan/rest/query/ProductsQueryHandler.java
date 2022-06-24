package com.sudarshan.rest.query;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sudarshan.core.data.ProductEntity;
import com.sudarshan.core.data.ProductRepository;
import com.sudarshan.rest.query.model.ProductRestModel;

@Component
public class ProductsQueryHandler {
	
	private ProductRepository productRepository;
	
	public ProductsQueryHandler(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	@QueryHandler
	public List<ProductRestModel> findProducts(FindProductsQuery findProductsQuery) {
		
		List<ProductRestModel> productRestModelList = new ArrayList<>();
		
		List<ProductEntity> productsEntity = productRepository.findAll();
		
		productsEntity.forEach(productEntity -> {
			ProductRestModel productRestModel = new ProductRestModel();
			BeanUtils.copyProperties(productEntity, productRestModel);
			productRestModelList.add(productRestModel);
		});
		
		return productRestModelList;
	}

}
