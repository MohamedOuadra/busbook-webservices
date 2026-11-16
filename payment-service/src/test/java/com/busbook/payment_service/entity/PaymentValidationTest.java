package com.busbook.payment_service.entity;

import com.busbook.payment_service.enums.PaymentMethod;
import com.busbook.payment_service.enums.PaymentStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenPaymentIsValid() {
        Payment payment = new Payment(1L, new BigDecimal("150.00"), PaymentMethod.CARTE_BANCAIRE);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("tx-12345");

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenReservationIdIsNull() {
        Payment payment = new Payment(null, new BigDecimal("150.00"), PaymentMethod.CARTE_BANCAIRE);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("tx-12345");

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenReservationIdIsZeroOrNegative() {
        Payment payment = new Payment(-1L, new BigDecimal("150.00"), PaymentMethod.CARTE_BANCAIRE);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("tx-12345");

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenAmountIsNull() {
        Payment payment = new Payment(1L, null, PaymentMethod.CARTE_BANCAIRE);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("tx-12345");

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenAmountIsZeroOrNegative() {
        Payment payment = new Payment(1L, BigDecimal.ZERO, PaymentMethod.CARTE_BANCAIRE);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("tx-12345");

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenPaymentMethodIsNull() {
        Payment payment = new Payment(1L, new BigDecimal("100.00"), null);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("tx-12345");

        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        assertFalse(violations.isEmpty());
    }
}
