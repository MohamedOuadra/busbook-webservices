package com.busbook.payment_service.repository;

import com.busbook.payment_service.entity.Payment;
import com.busbook.payment_service.enums.PaymentMethod;
import com.busbook.payment_service.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldSaveAndFindPaymentById() {
        Payment payment = new Payment(100L, new BigDecimal("120.50"), PaymentMethod.CARTE_BANCAIRE);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("tx-100");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setCompletedAt(LocalDateTime.now());

        Payment saved = paymentRepository.saveAndFlush(payment);

        assertNotNull(saved.getId());
        Optional<Payment> found = paymentRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(100L, found.get().getReservationId());
        assertEquals(new BigDecimal("120.50"), found.get().getAmount());
        assertEquals(PaymentStatus.SUCCESS, found.get().getStatus());
    }

    @Test
    void shouldFindByReservationId() {
        Payment payment = new Payment(200L, new BigDecimal("75.00"), PaymentMethod.PORTEFEUILLE_MOBILE);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("tx-200");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.saveAndFlush(payment);

        Optional<Payment> found = paymentRepository.findByReservationId(200L);
        assertTrue(found.isPresent());
        assertEquals("tx-200", found.get().getTransactionId());
    }

    @Test
    void shouldDetectExistingPaymentByReservationIdAndStatus() {
        Payment payment = new Payment(300L, new BigDecimal("90.00"), PaymentMethod.ESPECES);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("tx-300");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.saveAndFlush(payment);

        boolean exists = paymentRepository.existsByReservationIdAndStatus(300L, PaymentStatus.SUCCESS);
        assertTrue(exists);

        boolean existsFailed = paymentRepository.existsByReservationIdAndStatus(300L, PaymentStatus.FAILED);
        assertFalse(existsFailed);
    }
}
