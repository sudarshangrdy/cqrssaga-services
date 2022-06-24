package com.sudarshan.query.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<PaymentEntity, String> {

}
