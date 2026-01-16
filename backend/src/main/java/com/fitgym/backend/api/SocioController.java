package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.SocioLoginRequest;
import com.fitgym.backend.api.dto.SocioLoginResponse;
import com.fitgym.backend.api.dto.SocioSession;
import com.fitgym.backend.api.dto.SocioRegistroRequest;
import com.fitgym.backend.api.dto.SocioResponse;
import com.fitgym.backend.api.dto.EmailExistsResponse;
import com.fitgym.backend.api.dto.SocioUpdateRequest;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.service.InvalidCredentialsException;
import com.fitgym.backend.service.SocioService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Controlador REST para gestionar las operaciones relacionadas con los socios del sistema.
 * Proporciona endpoints para el registro y administración de socios en FitGym.
 */
@RestController
@RequestMapping("/api/socios")
public class SocioController {

  private final SocioService socioService;
  private static final String SESSION_SOCIO_KEY = "socioLogin";

  /**
  * Constructor del controlador de socios.
  * Inyecta el servicio de socios mediante inyección de dependencias.
  *
  * @param socioService servicio que gestiona la lógica de negocio de los socios
  */
  public SocioController(SocioService socioService) {
    this.socioService = socioService;
  }

  /**
   * Endpoint ligero para validar si un correo ya existe.
   */
  @GetMapping("/email-exists")
  public ResponseEntity<EmailExistsResponse> emailExists(@RequestParam String email) {
    boolean exists = socioService.emailExiste(email);
    return ResponseEntity.ok(new EmailExistsResponse(exists));
  }

/**
 * Registra un nuevo socio en el sistema FitGym.
 *
 * Realiza la creación de un nuevo perfil de socio con la información proporcionada,
 * validando todos los campos requeridos. Si el registro es exitoso, devuelve los datos
 * del socio creado junto con la URI de localización del recurso.
 *
 * @param req objeto {@link SocioRegistroRequest} que contiene los datos del socio a registrar:
 *            - nombre: nombre completo del socio
 *            - correoElectronico: correo electrónico único del socio
 *            - contrasena: contraseña encriptada del socio
 *            - telefono: número de teléfono de contacto
 *            - idTarifa: identificador de la tarifa a contratar
 *            - direccion: dirección domiciliaria
 *            - ciudad: ciudad de residencia
 *            - codigoPostal: código postal
 *
 * @return {@link ResponseEntity} con estado HTTP 201 (Created) y cuerpo {@link SocioResponse}
 *         que contiene:
 *         - id: identificador único del socio registrado
 *         - nombre: nombre del socio
 *         - correoElectronico: correo del socio
 *         - estado: estado actual del socio (ej: ACTIVO)
 *         - idTarifa: identificador de la tarifa asignada
 *         - nombreTarifa: nombre legible de la tarifa
 *         La respuesta incluye el header Location con la URI del nuevo recurso creado
 *
 * @throws ConstraintViolationException si algún campo no cumple con las validaciones requeridas
 * @throws EntityNotFoundException si la tarifa especificada no existe
 * @throws DataIntegrityViolationException si el correo electrónico ya está registrado
 */
  @PostMapping("/registro")
  public ResponseEntity<SocioResponse> registro(@Valid @RequestBody SocioRegistroRequest req) {
    Socio socio = socioService.registrar(
        req.nombre,
        req.correoElectronico,
        req.contrasena,
        req.telefono,
        req.idTarifa,
        req.direccion,
        req.ciudad,
        req.codigoPostal,
        req.paymentToken
    );

    SocioResponse body = new SocioResponse(
        socio.getId(),
        socio.getNombre(),
        socio.getCorreoElectronico(),
        socio.getEstado().name(),
        socio.getTarifa().getId(),
        socio.getTarifa().getNombre(),
        socio.getTelefono(),
        socio.getDireccion(),
        socio.getCiudad(),
        socio.getCodigoPostal()
    );

    return ResponseEntity
        .created(URI.create("/api/socios/" + socio.getId()))
        .body(body);
  }

  /**
   * Inicia sesion con correo y contrasena.
   *
   * Si las credenciales son correctas y el socio esta activo, crea una sesion HTTP
   * y devuelve los datos del socio para la UI.
   */
  @PostMapping("/login")
  public ResponseEntity<SocioLoginResponse> login(@Valid @RequestBody SocioLoginRequest req, HttpServletRequest request) {
    Socio socio = socioService.autenticar(req.correoElectronico, req.contrasena);

    SocioLoginResponse body = new SocioLoginResponse(
        socio.getId(),
        socio.getNombre(),
        socio.getCorreoElectronico(),
        socio.getEstado().name(),
        socio.getTarifa().getId(),
        socio.getTarifa().getNombre(),
        socio.getSaldoMonedero(),
        socio.getTelefono(),
        socio.getDireccion(),
        socio.getCiudad(),
        socio.getCodigoPostal()
    );

    HttpSession session = request.getSession(true);
    session.setAttribute(SESSION_SOCIO_KEY, SocioSession.fromLoginResponse(body));

    return ResponseEntity.ok(body);
  }

  /**
   * Devuelve la sesion actual si existe.
   *
   * Si no hay sesion activa, retorna 401 (Unauthorized).
   */
  @GetMapping("/me")
  public ResponseEntity<SocioLoginResponse> me(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      throw new InvalidCredentialsException("No hay sesion activa.");
    }

    Object value = session.getAttribute(SESSION_SOCIO_KEY);
    if (!(value instanceof SocioSession socioSession)) {
      throw new InvalidCredentialsException("No hay sesion activa.");
    }

    return ResponseEntity.ok(socioSession.toLoginResponse());
  }

  /**
   * Cierra sesion e invalida la sesion HTTP.
   */
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // editar perfil
  @PutMapping("/me")
  public ResponseEntity<SocioLoginResponse> updateMe(
          @Valid @RequestBody SocioUpdateRequest req,
          HttpServletRequest request
  ) {
      HttpSession session = request.getSession(false);
      if (session == null) {
          throw new InvalidCredentialsException("No hay sesion activa.");
      }

      Object value = session.getAttribute(SESSION_SOCIO_KEY);
      if (!(value instanceof SocioSession socioSession)) {
          throw new InvalidCredentialsException("No hay sesion activa.");
      }

      // actualizar bd
      Socio socio = socioService.actualizarDatosPersonales(
              socioSession.getId(),
              req.nombre(),
              req.telefono(),
              req.direccion(),
              req.ciudad(),
              req.codigoPostal()
      );

      SocioLoginResponse body = new SocioLoginResponse(
              socio.getId(),
              socio.getNombre(),
              socio.getCorreoElectronico(),
              socio.getEstado().name(),
              socio.getTarifa().getId(),
              socio.getTarifa().getNombre(),
              socio.getSaldoMonedero(),
              socio.getTelefono(),
              socio.getDireccion(),
              socio.getCiudad(),
              socio.getCodigoPostal()
      );

      session.setAttribute(SESSION_SOCIO_KEY, SocioSession.fromLoginResponse(body));

      return ResponseEntity.ok(body);
  }
}
