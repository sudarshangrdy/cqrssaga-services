package com.sudarshan.core.errorhandling;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;

public class ProductsServiceEventErrorHandler implements ListenerInvocationErrorHandler {

	@Override
	public void onError(Exception exception, EventMessage<?> event, EventMessageHandler eventHandler) throws Exception {
		throw exception;		
	}

}
