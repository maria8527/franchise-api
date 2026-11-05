package com.trujillo.franchise_api.infrastructure.entrypoint;

import com.trujillo.franchise_api.domain.exception.ResourceNotFoundException;
import com.trujillo.franchise_api.infrastructure.entrypoint.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage());
    }

    // Falló una validación del DTO (@NotBlank, @NotNull, @PositiveOrZero)
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(WebExchangeBindException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return new ErrorResponse(400, String.join(", ", errors));
    }

    // JSON mal formado o ilegible
    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadInput(ServerWebInputException ex) {
        return new ErrorResponse(400, "Invalid request body");
    }

    // Regla de negocio del dominio (ej. stock negativo)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        return new ErrorResponse(400, ex.getMessage());
    }

    // Cualquier otro error: se registra en el log y no se expone el detalle al cliente
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return new ErrorResponse(500, "Internal server error");
    }
}
