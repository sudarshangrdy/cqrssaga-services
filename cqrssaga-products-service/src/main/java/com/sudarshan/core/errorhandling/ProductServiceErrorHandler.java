package com.sudarshan.core.errorhandling;

import java.util.Date;

import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ProductServiceErrorHandler {
	
	@ExceptionHandler(value = IllegalStateException.class)
	public ResponseEntity<ErrorMessage> handleIllegalStateException(IllegalStateException illegalStateException, WebRequest webRequest) {
		return new ResponseEntity<ErrorMessage>(new ErrorMessage(new Date(), illegalStateException.getLocalizedMessage()) , new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> handleException(Exception exception, WebRequest webRequest) {
		return new ResponseEntity<Object>(new ErrorMessage(new Date(), exception.getLocalizedMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = CommandExecutionException.class)
	public ResponseEntity<Object> handleCommandExecutionException(CommandExecutionException commandExecutionException, WebRequest webRequest) {
		return new ResponseEntity<Object>(new ErrorMessage(new Date(), commandExecutionException.getLocalizedMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}


}
