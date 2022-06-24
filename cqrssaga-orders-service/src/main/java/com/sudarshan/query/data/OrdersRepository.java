package com.sudarshan.query.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<OrderEntity, String>{
	
	OrderEntity findByOrderId(String orderId);

}	
