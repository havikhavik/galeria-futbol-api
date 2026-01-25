package com.galeriafutbol.api.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, WebRequest request,
            List<String> errors) {
        String path = (request instanceof ServletWebRequest)
                ? ((ServletWebRequest) request).getRequest().getRequestURI()
                : null;
        ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message, path, errors);
        return new ResponseEntity<>(body, new HttpHeaders(), status);
    }

    // 400 – @Valid (body DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> onMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest req) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.toList());
        return build(HttpStatus.BAD_REQUEST, "Error de validación", req, details);
    }

    // 400 – @Validated (params/path)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> onConstraintViolation(ConstraintViolationException ex, WebRequest req) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage).collect(Collectors.toList());
        return build(HttpStatus.BAD_REQUEST, "Violación de restricción", req, details);
    }

    // 400 – JSON mal formado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> onNotReadable(HttpMessageNotReadableException ex, WebRequest req) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        return build(HttpStatus.BAD_REQUEST, "JSON mal formado en la solicitud", req, List.of(msg));
    }

    // 400 – Falta un query param
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> onMissingParam(MissingServletRequestParameterException ex, WebRequest req) {
        return build(HttpStatus.BAD_REQUEST,
                "Falta el parámetro requerido: " + ex.getParameterName(), req, null);
    }

    // 400 – Error propio de request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> onBadRequest(BadRequestException ex, WebRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
    }

    // 401 – Login malo
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> onBadCredentials(BadCredentialsException ex, WebRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", req, null);
    }

    // 401 – Sin token/expirado detectado por lógica
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> onUnauthorized(UnauthorizedException ex, WebRequest req) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null);
    }

    // 403 – Sin permisos
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> onAccessDenied(AccessDeniedException ex, WebRequest req) {
        return build(HttpStatus.FORBIDDEN, "Acceso denegado", req, null);
    }

    // 404 – No existe
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> onNotFound(ResourceNotFoundException ex, WebRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    // 405 – Método no permitido
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> onMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest req) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req, null);
    }

    // 409 – Conflicto de integridad de datos (UNIQUE / FK, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> onDataIntegrity(DataIntegrityViolationException ex, WebRequest req) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        return build(HttpStatus.CONFLICT, "Conflicto de integridad de datos", req, List.of(msg));
    }

    // 409 – Conflicto funcional no-DB
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> onConflict(ConflictException ex, WebRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    // 422 – Regla de negocio
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> onBusiness(BusinessRuleException ex, WebRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req, null);
    }

    // 500 – Cualquier otra cosa
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> onAll(Exception ex, WebRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req, null);
    }
}
