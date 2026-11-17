package com.busbook.auth_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global des exceptions pour le service d'authentification.
 */
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

    // 400 : Erreurs de validation de requête
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiError> handleValidationException(
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

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // 409 : Conflit (par exemple email déjà existant)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleConflictException(
            IllegalArgumentException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    // 401 : Erreur d'authentification ou mauvais identifiants
    @ExceptionHandler({
            BadCredentialsException.class,
            AuthenticationException.class
    })
    public ResponseEntity<ApiError> handleAuthenticationException(
            Exception exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect.", request);
    }

    // 404 : Utilisateur non trouvé
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(
            UsernameNotFoundException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    // 500 : Erreur interne non gérée
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(
            Exception exception,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur inattendue est survenue.",
                request
        );
    }
}
