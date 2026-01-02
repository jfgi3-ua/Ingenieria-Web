package com.fitgym.backend.api.dto;

import java.io.Serializable;

/**
 * DTO interno para almacenar datos de sesion en HttpSession.
 *
 * Se separa de la respuesta de login para evitar acoplar la API
 * con el formato de almacenamiento en sesion y para garantizar
 * serializacion segura. Esto arregla los warnings que aparecían.
 */
public class SocioSession implements Serializable {
  private static final long serialVersionUID = 1L;

  private final Long id;
  private final String nombre;
  private final String correoElectronico;
  private final String estado;
  private final Long idTarifa;
  private final String tarifaNombre;

  public SocioSession(
      Long id,
      String nombre,
      String correoElectronico,
      String estado,
      Long idTarifa,
      String tarifaNombre
  ) {
    this.id = id;
    this.nombre = nombre;
    this.correoElectronico = correoElectronico;
    this.estado = estado;
    this.idTarifa = idTarifa;
    this.tarifaNombre = tarifaNombre;
  }

  public static SocioSession fromLoginResponse(SocioLoginResponse response) {
    return new SocioSession(
        response.id(),
        response.nombre(),
        response.correoElectronico(),
        response.estado(),
        response.idTarifa(),
        response.tarifaNombre()
    );
  }

  public SocioLoginResponse toLoginResponse() {
    return new SocioLoginResponse(id, nombre, correoElectronico, estado, idTarifa, tarifaNombre);
  }
}
