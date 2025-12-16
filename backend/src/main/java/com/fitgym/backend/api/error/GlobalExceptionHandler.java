package com.fitgym.backend.api.error;

import com.fitgym.backend.service.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/**
 * Manejador global de excepciones para la aplicación REST.
 *
 * Esta clase intercepta y procesa las excepciones lanzadas en los controladores REST,
 * proporcionando respuestas de error consistentes y estructuradas al cliente.
 *
 * Las excepciones se clasifican en tres categorías:
 * - Excepciones de negocio: errores controlados relacionados con la lógica de negocio
 * - Excepciones de validación: errores en la validación de datos de entrada
 * - Excepciones genéricas: errores inesperados no contemplados
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de negocio (BusinessException).
     *
     * Retorna una respuesta HTTP 400 (Bad Request) con el mensaje de error específico
     * proporcionado por la excepción de negocio.
     *
     * @param ex excepción de negocio capturada
     * @param req objeto de la solicitud HTTP actual
     * @return respuesta HTTP con estado 400 y detalles del error
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    /**
     * Maneja excepciones de validación de formularios.
     *
     * Captura errores de validación tanto de argumentos de métodos como de binding de datos.
     * Retorna una respuesta HTTP 400 (Bad Request) con un mensaje genérico de validación fallida.
     *
     * @param ex excepción de validación capturada (MethodArgumentNotValidException o BindException)
     * @param req objeto de la solicitud HTTP actual
     * @return respuesta HTTP con estado 400 y mensaje de validación
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiError> handleValidation(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Validación fallida. Revisa los campos del formulario.", req.getRequestURI());
    }

    /**
     * Maneja todas las excepciones genéricas no capturadas por otros manejadores.
     *
     * Funciona como último filtro de seguridad, retornando una respuesta HTTP 500 (Internal Server Error)
     * con un mensaje genérico para evitar exponer detalles técnicos al cliente.
     *
     * @param ex excepción genérica capturada
     * @param req objeto de la solicitud HTTP actual
     * @return respuesta HTTP con estado 500 y mensaje de error inesperado
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado.", req.getRequestURI());
    }

    /**
     * Construye una respuesta de error estructurada.
     *
     * Crea un objeto ApiError con toda la información necesaria (marca de tiempo, código HTTP,
     * razón HTTP, mensaje descriptivo y ruta solicitada) y lo encapsula en una ResponseEntity
     * con el código de estado HTTP correspondiente.
     *
     * @param status código de estado HTTP a retornar
     * @param message mensaje descriptivo del error
     * @param path ruta URI de la solicitud que generó el error
     * @return ResponseEntity con el objeto ApiError y el estado HTTP especificado
     */
    private ResponseEntity<ApiError> build(HttpStatus status, String message, String path) {
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path);
        return ResponseEntity.status(status).body(body);
    }
}
