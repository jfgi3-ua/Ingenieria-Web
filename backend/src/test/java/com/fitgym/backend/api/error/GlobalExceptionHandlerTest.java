package com.fitgym.backend.api.error;

import com.fitgym.backend.service.BusinessException;
import com.fitgym.backend.service.DuplicateEmailException;
import com.fitgym.backend.service.TarifaNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Clase de prueba para {@link GlobalExceptionHandler}.
 *
 * Verifica el comportamiento correcto del manejador global de excepciones,
 * asegurando que cada tipo de excepción sea mapeado al código de estado HTTP
 * correspondiente.
 *
 * Las pruebas cubren los siguientes casos:
 * - {@link BusinessException}: Retorna estado BAD_REQUEST (400)
 * - {@link DuplicateEmailException}: Retorna estado CONFLICT (409)
 * - {@link TarifaNotFoundException}: Retorna estado NOT_FOUND (404)
 *
 * @see GlobalExceptionHandler
 * @see BusinessException
 * @see DuplicateEmailException
 * @see TarifaNotFoundException
 * @see ApiError
 */
class GlobalExceptionHandlerTest {

  /**
   * Verifica que, cuando se lanza una {@link BusinessException},
   * el {@link GlobalExceptionHandler} devuelva una respuesta con
   * código de estado {@link HttpStatus#BAD_REQUEST} (400).
   */
  @Test
  void handleBusiness_returnsBadRequest_andApiErrorBody() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/socios/registro");

    ResponseEntity<ApiError> res = handler.handleBusiness(new BusinessException("Error"), req);

    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());

    ApiError error = res.getBody();
    assertNotNull(error);

    assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
    assertEquals("Error", error.getMessage());
    assertEquals("/api/socios/registro", error.getPath());
    assertNotNull(error.getTimestamp());
  }

  /**
   * Verifica que, cuando se lanza una {@link DuplicateEmailException},
   * el {@link GlobalExceptionHandler} devuelva una respuesta con
   * código de estado {@link HttpStatus#CONFLICT} (409).
   */
  @Test
  void handleDuplicateEmail_returnsConflict_andApiErrorBody() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/socios/registro");

    ResponseEntity<ApiError> res = handler.handleDuplicateEmail(new DuplicateEmailException("Duplicado"), req);

    assertEquals(HttpStatus.CONFLICT, res.getStatusCode());

    ApiError error = res.getBody();
    assertEquals(HttpStatus.CONFLICT.value(), error.getStatus());
    assertEquals("Duplicado", error.getMessage());
    assertEquals("/api/socios/registro", error.getPath());
    assertNotNull(error.getTimestamp());
  }

  /**
   * Verifica que, cuando se lanza una {@link TarifaNotFoundException},
   * el {@link GlobalExceptionHandler} devuelva una respuesta con
   * código de estado {@link HttpStatus#NOT_FOUND} (404).
   */
  @Test
  void handleTarifaNotFound_returnsNotFound_andApiErrorBody() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/socios/registro");

    ResponseEntity<ApiError> res = handler.handleTarifaNotFound(new TarifaNotFoundException("No existe"), req);

    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

    ApiError error = res.getBody();
    assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
    assertEquals("No existe", error.getMessage());
    assertEquals("/api/socios/registro", error.getPath());
    assertNotNull(error.getTimestamp());
  }

  /**
   * Verifica que, cuando se produce una excepción genérica no controlada,
   * el {@link GlobalExceptionHandler} devuelva una respuesta con código de
   * estado {@link HttpStatus#INTERNAL_SERVER_ERROR} (500) y un mensaje
   * genérico y seguro para el cliente.
   *
   * También comprueba que la ruta solicitada y la marca temporal se
   * establezcan correctamente en el objeto {@link ApiError}.
   */
  @Test
  void handleGeneric_returnsInternalServerError_andSafeMessage() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/algo");

    ResponseEntity<ApiError> res = handler.handleGeneric(new RuntimeException("boom"), req);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());

    ApiError error = res.getBody();
    assertNotNull(error);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getStatus());
    assertEquals("Error inesperado.", error.getMessage());
    assertEquals("/api/algo", error.getPath());
    assertNotNull(error.getTimestamp());
  }
}
