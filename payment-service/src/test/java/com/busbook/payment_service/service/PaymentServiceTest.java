package com.busbook.payment_service.service;

import com.busbook.payment_service.client.ReservationClient;
import com.busbook.payment_service.dto.PaymentRequest;
import com.busbook.payment_service.dto.PaymentResponse;
import com.busbook.payment_service.entity.Payment;
import com.busbook.payment_service.enums.PaymentMethod;
import com.busbook.payment_service.enums.PaymentStatus;
import com.busbook.payment_service.exception.PaymentProcessingException;
import com.busbook.payment_service.exception.ResourceNotFoundException;
import com.busbook.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationClient reservationClient;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldProcessPaymentSuccessfully() {
        PaymentRequest request = new PaymentRequest(1L, new BigDecimal("150.00"), PaymentMethod.CARTE_BANCAIRE);

        when(paymentRepository.existsByReservationIdAndStatus(1L, PaymentStatus.SUCCESS))
                .thenReturn(false);
        when(reservationClient.confirmReservation(1L)).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            ReflectionTestUtils.setField(p, "id", 10L);
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getReservationId());
        assertEquals(new BigDecimal("150.00"), response.getAmount());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertNotNull(response.getTransactionId());
        verify(reservationClient).confirmReservation(1L);
        verify(reservationClient, never()).cancelReservation(any());
    }

    @Test
    void shouldHandleSimulatedPaymentFailure() {
        PaymentRequest request = new PaymentRequest(
                1L, new BigDecimal("150.00"), PaymentMethod.CARTE_BANCAIRE, true
        );

        when(paymentRepository.existsByReservationIdAndStatus(1L, PaymentStatus.SUCCESS))
                .thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getStatus());
        verify(reservationClient).cancelReservation(1L);
        verify(reservationClient, never()).confirmReservation(any());
    }

    @Test
    void shouldThrowWhenReservationConfirmationFailsAfterPayment() {
        PaymentRequest request = new PaymentRequest(1L, new BigDecimal("150.00"), PaymentMethod.CARTE_BANCAIRE);

        when(paymentRepository.existsByReservationIdAndStatus(1L, PaymentStatus.SUCCESS))
                .thenReturn(false);
        // La réservation a expiré côté reservation-service
        when(reservationClient.confirmReservation(1L)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentProcessingException ex = assertThrows(
                PaymentProcessingException.class,
                () -> paymentService.processPayment(request)
        );

        assertTrue(ex.getMessage().contains("Impossible de confirmer la réservation"));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldRejectPaymentWhenAlreadyPaid() {
        PaymentRequest request = new PaymentRequest(1L, new BigDecimal("150.00"), PaymentMethod.CARTE_BANCAIRE);

        when(paymentRepository.existsByReservationIdAndStatus(1L, PaymentStatus.SUCCESS))
                .thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> paymentService.processPayment(request)
        );

        assertEquals("Un paiement validé existe déjà pour cette réservation.", ex.getMessage());
        verify(reservationClient, never()).confirmReservation(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void shouldRejectNullPaymentRequest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(null)
        );
    }

    @Test
    void shouldRejectInvalidReservationId() {
        PaymentRequest request = new PaymentRequest(0L, new BigDecimal("150.00"));

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(request)
        );
    }

    @Test
    void shouldRejectZeroOrNegativeAmount() {
        PaymentRequest request = new PaymentRequest(1L, BigDecimal.ZERO);

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(request)
        );
    }

    @Test
    void shouldGetPaymentById() {
        Payment payment = new Payment(1L, new BigDecimal("100.00"), PaymentMethod.CARTE_BANCAIRE);
        ReflectionTestUtils.setField(payment, "id", 5L);
        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findById(5L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentById(5L);

        assertNotNull(response);
        assertEquals(5L, response.getId());
        assertEquals(1L, response.getReservationId());
    }

    @Test
    void shouldThrowWhenPaymentNotFoundById() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.getPaymentById(99L)
        );
    }

    @Test
    void shouldGetPaymentByReservationId() {
        Payment payment = new Payment(12L, new BigDecimal("100.00"), PaymentMethod.CARTE_BANCAIRE);
        ReflectionTestUtils.setField(payment, "id", 7L);
        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByReservationId(12L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentByReservationId(12L);

        assertNotNull(response);
        assertEquals(7L, response.getId());
        assertEquals(12L, response.getReservationId());
    }

    @Test
    void shouldThrowWhenPaymentNotFoundByReservationId() {
        when(paymentRepository.findByReservationId(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.getPaymentByReservationId(999L)
        );
    }
}
