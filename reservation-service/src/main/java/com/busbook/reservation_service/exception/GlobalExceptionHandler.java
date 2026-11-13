package com.busbook.reservation_service.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

    // 400 : données invalides
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(
            Exception exception,
            HttpServletRequest request) {

        String message = exception.getMessage();

        if (exception instanceof MethodArgumentNotValidException validationException) {
            message = validationException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .findFirst()
                    .orElse("Les données fournies sont invalides.");
        }

        if (message == null || message.isBlank()) {
            message = "Les données fournies sont invalides.";
        }

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    // 404 : ressource introuvable
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    // 409 : conflit métier ou réservation concurrente
    @ExceptionHandler({
            IllegalStateException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ApiError> handleConflict(
            Exception exception,
            HttpServletRequest request) {

        String message = exception instanceof DataIntegrityViolationException
                ? "Ce siège est déjà réservé pour ce trajet."
                : exception.getMessage();

        return buildResponse(
                HttpStatus.CONFLICT,
                message,
                request
        );
    }

    // 500 : erreur inattendue
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleInternalError(
            Exception exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur interne est survenue.",
                request
        );
    }
}