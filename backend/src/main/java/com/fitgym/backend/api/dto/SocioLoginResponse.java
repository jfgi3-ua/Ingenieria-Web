package com.fitgym.backend.api.dto;

/**
 * DTO de salida para inicio de sesion.
 *
 * Contiene los datos del socio que la UI necesita para mostrar la sesión
 * sin exponer la contraseña.
 *
 * Debe ser serializable porque se almacena en HttpSession.
 */
public record SocioLoginResponse(
    Long id,
    String nombre,
    String correoElectronico,
    String estado,
    Long idTarifa,
    String tarifaNombre
) implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
}
