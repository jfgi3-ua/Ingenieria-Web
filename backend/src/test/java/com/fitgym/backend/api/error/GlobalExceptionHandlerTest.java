package com.fitgym.backend.api.error;

import com.fitgym.backend.service.BusinessException;
import com.fitgym.backend.service.DuplicateEmailException;
import com.fitgym.backend.service.InvalidCredentialsException;
import com.fitgym.backend.service.PagoRegistroNoCompletadoException;
import com.fitgym.backend.service.PagoRegistroNotFoundException;
import com.fitgym.backend.service.SocioInactivoException;
import com.fitgym.backend.service.TarifaNotFoundException;
import com.fitgym.backend.service.TpvvCommunicationException;
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

  @Test
  void handlePagoNotFound_returnsNotFound_andApiErrorBody() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/pagos/verify/tok");

    ResponseEntity<ApiError> res = handler.handlePagoNotFound(
        new PagoRegistroNotFoundException("No existe"), req);

    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());

    ApiError error = res.getBody();
    assertNotNull(error);
    assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
    assertEquals("No existe", error.getMessage());
    assertEquals("/api/pagos/verify/tok", error.getPath());
    assertNotNull(error.getTimestamp());
  }

  @Test
  void handlePagoNoCompletado_returnsConflict_andApiErrorBody() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/socios/registro");

    ResponseEntity<ApiError> res = handler.handlePagoNoCompletado(
        new PagoRegistroNoCompletadoException("Pago pendiente"), req);

    assertEquals(HttpStatus.CONFLICT, res.getStatusCode());

    ApiError error = res.getBody();
    assertNotNull(error);
    assertEquals(HttpStatus.CONFLICT.value(), error.getStatus());
    assertEquals("Pago pendiente", error.getMessage());
    assertEquals("/api/socios/registro", error.getPath());
    assertNotNull(error.getTimestamp());
  }

  @Test
  void handleTpvv_returnsBadGateway_andApiErrorBody() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/pagos/init");

    ResponseEntity<ApiError> res = handler.handleTpvv(
        new TpvvCommunicationException("TPVV error"), req);

    assertEquals(HttpStatus.BAD_GATEWAY, res.getStatusCode());

    ApiError error = res.getBody();
    assertNotNull(error);
    assertEquals(HttpStatus.BAD_GATEWAY.value(), error.getStatus());
    assertEquals("TPVV error", error.getMessage());
    assertEquals("/api/pagos/init", error.getPath());
    assertNotNull(error.getTimestamp());
  }

  /**
   * Verifica que, cuando se lanza una {@link InvalidCredentialsException},
   * el {@link GlobalExceptionHandler} devuelva 401 (Unauthorized).
   */
  @Test
  void handleInvalidCredentials_returnsUnauthorized_andApiErrorBody() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/socios/login");

    ResponseEntity<ApiError> res = handler.handleInvalidCredentials(
        new InvalidCredentialsException("Credenciales invalidas."), req);

    assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());

    ApiError error = res.getBody();
    assertNotNull(error);
    assertEquals(HttpStatus.UNAUTHORIZED.value(), error.getStatus());
    assertEquals("Credenciales invalidas.", error.getMessage());
    assertEquals("/api/socios/login", error.getPath());
    assertNotNull(error.getTimestamp());
  }

  /**
   * Verifica que, cuando se lanza una {@link SocioInactivoException},
   * el {@link GlobalExceptionHandler} devuelva 403 (Forbidden).
   */
  @Test
  void handleSocioInactivo_returnsForbidden_andApiErrorBody() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/socios/login");

    ResponseEntity<ApiError> res = handler.handleSocioInactivo(
        new SocioInactivoException("Socio inactivo."), req);

    assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());

    ApiError error = res.getBody();
    assertNotNull(error);
    assertEquals(HttpStatus.FORBIDDEN.value(), error.getStatus());
    assertEquals("Socio inactivo.", error.getMessage());
    assertEquals("/api/socios/login", error.getPath());
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
