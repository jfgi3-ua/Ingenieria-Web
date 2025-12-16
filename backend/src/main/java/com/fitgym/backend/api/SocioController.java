package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.SocioRegistroRequest;
import com.fitgym.backend.api.dto.SocioResponse;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.service.SocioService;
import jakarta.validation.Valid;
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
        req.codigoPostal
    );

    SocioResponse body = new SocioResponse(
        socio.getId(),
        socio.getNombre(),
        socio.getCorreoElectronico(),
        socio.getEstado().name(),
        socio.getTarifa().getId(),
        socio.getTarifa().getNombre()
    );

    return ResponseEntity
        .created(URI.create("/api/socios/" + socio.getId()))
        .body(body);
  }
}
