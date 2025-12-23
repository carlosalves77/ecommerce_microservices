package com.carldev.payment_service.repository;

import com.carldev.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {


    @Query(value = "SELECT * FROM payment_table p WHERE :user_name % ANY(STRING_TO_ARRAY(p.user_name, ' '))",
            nativeQuery = true)
    Optional<List<Payment>> findByUserName(@Param("user_name") String userName);
}
