package com.busbook.payment_service.repository;

import com.busbook.payment_service.entity.Payment;
import com.busbook.payment_service.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(Long reservationId);

    List<Payment> findAllByReservationId(Long reservationId);

    Optional<Payment> findByTransactionId(String transactionId);

    boolean existsByReservationIdAndStatus(Long reservationId, PaymentStatus status);
}
