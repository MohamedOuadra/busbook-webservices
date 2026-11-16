package com.busbook.payment_service.controller;

import com.busbook.payment_service.dto.PaymentRequest;
import com.busbook.payment_service.dto.PaymentResponse;
import com.busbook.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Traiter un paiement.
     * POST /api/payments
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.processPayment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Consulter un paiement par son ID.
     * GET /api/payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable("id") Long id) {

        PaymentResponse response = paymentService.getPaymentById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Consulter le paiement associé à une réservation.
     * GET /api/payments/reservation/{reservationId}
     */
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<PaymentResponse> getPaymentByReservationId(
            @PathVariable("reservationId") Long reservationId) {

        PaymentResponse response = paymentService.getPaymentByReservationId(reservationId);

        return ResponseEntity.ok(response);
    }
}
