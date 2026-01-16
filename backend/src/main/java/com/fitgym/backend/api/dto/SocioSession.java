package com.fitgym.backend.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fitgym.backend.api.dto.MembresiaResponse;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

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
  private final BigDecimal saldoMonedero;
  private final String telefono;
  private final String direccion;
  private final String ciudad;
  private final String codigoPostal;

  public SocioSession(
      Long id,
      String nombre,
      String correoElectronico,
      String estado,
      Long idTarifa,
      String tarifaNombre,
      BigDecimal saldoMonedero,
      String telefono,
      String direccion,
      String ciudad,
      String codigoPostal
  ) {
    this.id = id;
    this.nombre = nombre;
    this.correoElectronico = correoElectronico;
    this.estado = estado;
    this.idTarifa = idTarifa;
    this.tarifaNombre = tarifaNombre;
    this.saldoMonedero = saldoMonedero;
    this.telefono = telefono;
    this.direccion = direccion;
    this.ciudad = ciudad;
    this.codigoPostal = codigoPostal;
  }

  public static SocioSession fromLoginResponse(SocioLoginResponse response) {
    return new SocioSession(
        response.id(),
        response.nombre(),
        response.correoElectronico(),
        response.estado(),
        response.idTarifa(),
        response.tarifaNombre(),
        response.saldoMonedero(),
        response.telefono(),
        response.direccion(),
        response.ciudad(),
        response.codigoPostal()
    );
  }

  public SocioLoginResponse toLoginResponse() {
    return new SocioLoginResponse(id, nombre, correoElectronico, estado, idTarifa, tarifaNombre, saldoMonedero,
            telefono, direccion, ciudad, codigoPostal);
  }

    public Long getId() {
        return id;
    }
}
