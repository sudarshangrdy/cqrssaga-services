package com.sudarshan.query.events;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.sudarshan.core.model.PaymentDetails;
import com.sudarshan.core.model.User;
import com.sudarshan.core.query.FetchUserPaymentDetailsQuery;

@Component
public class UserEventsHandler {
	
	@QueryHandler
	public User fetchUserDetails(FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery) {
		PaymentDetails paymentDetails = PaymentDetails.builder()
				.cardNumber("123Card")
				.cvv("123")
				.name("SERGEY KARGOPOLOV")
				.validUntilMonth(12)
				.validUntilYear(2030)
				.build();
				 
		User userRest = User.builder()
				.firstName("Sergey")
				.lastName("Kargopolov")
				.userId(fetchUserPaymentDetailsQuery.getUserId())
				.paymentDetails(paymentDetails)
				.build();
				
		return userRest;
	}

}
