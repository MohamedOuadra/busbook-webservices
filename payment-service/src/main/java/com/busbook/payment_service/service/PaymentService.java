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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationClient reservationClient;

    public PaymentService(
            PaymentRepository paymentRepository,
            ReservationClient reservationClient) {

        this.paymentRepository = paymentRepository;
        this.reservationClient = reservationClient;
    }

    /**
     * Traite une demande de paiement pour une réservation.
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {

        validatePaymentRequest(request);

        // Vérifier si un paiement validé existe déjà pour cette réservation
        if (paymentRepository.existsByReservationIdAndStatus(
                request.getReservationId(),
                PaymentStatus.SUCCESS)) {

            throw new IllegalStateException(
                    "Un paiement validé existe déjà pour cette réservation."
            );
        }

        Payment payment = new Payment();
        payment.setReservationId(request.getReservationId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(
                request.getPaymentMethod() != null
                        ? request.getPaymentMethod()
                        : PaymentMethod.CARTE_BANCAIRE
        );
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());

        // Cas de simulation d'échec de transaction bancaire
        if (Boolean.TRUE.equals(request.getSimulateFailure())) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setCompletedAt(LocalDateTime.now());
            Payment saved = paymentRepository.save(payment);

            // Annuler la réservation côté reservation-service
            reservationClient.cancelReservation(request.getReservationId());

            return PaymentResponse.fromEntity(saved);
        }

        // Tenter de confirmer la réservation auprès de reservation-service
        boolean confirmed = reservationClient.confirmReservation(request.getReservationId());

        if (!confirmed) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            throw new PaymentProcessingException(
                    "Impossible de confirmer la réservation (délai de 10 minutes expiré ou réservation introuvable)."
            );
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCompletedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.fromEntity(saved);
    }

    /**
     * Récupère un paiement par son identifiant.
     */
    public PaymentResponse getPaymentById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'identifiant du paiement doit être positif.");
        }

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement introuvable."));

        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Récupère le paiement associé à une réservation.
     */
    public PaymentResponse getPaymentByReservationId(Long reservationId) {

        if (reservationId == null || reservationId <= 0) {
            throw new IllegalArgumentException("L'identifiant de réservation doit être positif.");
        }

        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun paiement trouvé pour cette réservation."));

        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Valide les données requises pour le paiement.
     */
    private void validatePaymentRequest(PaymentRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Les informations de paiement sont obligatoires.");
        }

        if (request.getReservationId() == null || request.getReservationId() <= 0) {
            throw new IllegalArgumentException("L'identifiant de réservation doit être positif.");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit être supérieur à 0.");
        }
    }
}
