package com.busbook.payment_service.dto;

import com.busbook.payment_service.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull(message = "L'identifiant de réservation est obligatoire")
    @Positive(message = "L'identifiant de réservation doit être positif")
    private Long reservationId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private Boolean simulateFailure;

    public PaymentRequest() {
    }

    public PaymentRequest(Long reservationId, BigDecimal amount) {
        this(reservationId, amount, PaymentMethod.CARTE_BANCAIRE, false);
    }

    public PaymentRequest(Long reservationId, BigDecimal amount, PaymentMethod paymentMethod) {
        this(reservationId, amount, paymentMethod, false);
    }

    public PaymentRequest(Long reservationId, BigDecimal amount, PaymentMethod paymentMethod, Boolean simulateFailure) {
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.simulateFailure = simulateFailure;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod != null ? paymentMethod : PaymentMethod.CARTE_BANCAIRE;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getSimulateFailure() {
        return simulateFailure != null && simulateFailure;
    }

    public void setSimulateFailure(Boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
