package com.busbook.payment_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HttpReservationClient implements ReservationClient {

    private static final Logger log = LoggerFactory.getLogger(HttpReservationClient.class);

    private final RestClient restClient;

    public HttpReservationClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.reservation-service.url:http://localhost:8082}") String reservationServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(reservationServiceUrl)
                .build();
    }

    @Override
    public boolean confirmReservation(Long reservationId) {
        try {
            return restClient.patch()
                    .uri("/api/reservations/{id}/confirm", reservationId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, resp) -> {
                        log.warn("Échec confirmation réservation {}: code HTTP {}", reservationId, resp.getStatusCode());
                    })
                    .toBodilessEntity()
                    .getStatusCode()
                    .is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erreur lors de l'appel confirmation réservation {}: {}", reservationId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cancelReservation(Long reservationId) {
        try {
            return restClient.patch()
                    .uri("/api/reservations/{id}/cancel", reservationId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, resp) -> {
                        log.warn("Échec annulation réservation {}: code HTTP {}", reservationId, resp.getStatusCode());
                    })
                    .toBodilessEntity()
                    .getStatusCode()
                    .is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erreur lors de l'appel annulation réservation {}: {}", reservationId, e.getMessage());
            return false;
        }
    }
}
