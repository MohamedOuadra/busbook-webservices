package com.busbook.payment_service.controller;

import com.busbook.payment_service.dto.PaymentRequest;
import com.busbook.payment_service.dto.PaymentResponse;
import com.busbook.payment_service.enums.PaymentMethod;
import com.busbook.payment_service.enums.PaymentStatus;
import com.busbook.payment_service.exception.GlobalExceptionHandler;
import com.busbook.payment_service.exception.ResourceNotFoundException;
import com.busbook.payment_service.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private PaymentResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        sampleResponse = new PaymentResponse(
                1L,
                10L,
                new BigDecimal("150.00"),
                PaymentMethod.CARTE_BANCAIRE,
                PaymentStatus.SUCCESS,
                "tx-test-123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void processPayment_ShouldReturn201() throws Exception {
        PaymentRequest request = new PaymentRequest(10L, new BigDecimal("150.00"), PaymentMethod.CARTE_BANCAIRE);

        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/payments")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservationId").value(10))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentService).processPayment(any(PaymentRequest.class));
    }

    @Test
    void processPayment_WhenInvalidRequest_ShouldReturn400() throws Exception {
        PaymentRequest request = new PaymentRequest(0L, new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/payments")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void processPayment_WhenConflict_ShouldReturn409() throws Exception {
        PaymentRequest request = new PaymentRequest(10L, new BigDecimal("150.00"));

        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenThrow(new IllegalStateException("Un paiement validé existe déjà pour cette réservation."));

        mockMvc.perform(post("/api/payments")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Un paiement validé existe déjà pour cette réservation."));
    }

    @Test
    void getPaymentById_ShouldReturn200() throws Exception {
        when(paymentService.getPaymentById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservationId").value(10))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentService).getPaymentById(1L);
    }

    @Test
    void getPaymentById_WhenNotFound_ShouldReturn404() throws Exception {
        when(paymentService.getPaymentById(99L))
                .thenThrow(new ResourceNotFoundException("Paiement introuvable."));

        mockMvc.perform(get("/api/payments/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Paiement introuvable."));
    }

    @Test
    void getPaymentByReservationId_ShouldReturn200() throws Exception {
        when(paymentService.getPaymentByReservationId(10L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/payments/reservation/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservationId").value(10));

        verify(paymentService).getPaymentByReservationId(10L);
    }

    @Test
    void getPaymentByReservationId_WhenNotFound_ShouldReturn404() throws Exception {
        when(paymentService.getPaymentByReservationId(999L))
                .thenThrow(new ResourceNotFoundException("Aucun paiement trouvé pour cette réservation."));

        mockMvc.perform(get("/api/payments/reservation/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
