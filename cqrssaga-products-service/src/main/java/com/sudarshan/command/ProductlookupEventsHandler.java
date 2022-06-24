package com.sudarshan.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;

import com.sudarshan.core.data.ProductLookupEntity;
import com.sudarshan.core.data.ProductLookupRepository;
import com.sudarshan.core.events.ProductCreatedEvent;

@Component
@ProcessingGroup("createProduct") //This event handler should at the command api server
public class ProductlookupEventsHandler {
	
	private final ProductLookupRepository productLookupRepository;
	
	public ProductlookupEventsHandler(ProductLookupRepository productLookupRepository) {
		this.productLookupRepository = productLookupRepository;
	}
	
	@EventHandler
	public void on(ProductCreatedEvent productCreatedEvent) {
		ProductLookupEntity productLookupEntity = new ProductLookupEntity(productCreatedEvent.getProductId(), productCreatedEvent.getTitle());
		productLookupRepository.save(productLookupEntity);
	}
	
	@ResetHandler //This method is called when reset of token is initialized for replay of events
	public void reset() {
		productLookupRepository.deleteAll();
	}

}
