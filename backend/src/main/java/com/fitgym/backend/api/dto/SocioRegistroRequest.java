package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Solicitud de registro de socio.
 */
public class SocioRegistroRequest {

  @NotBlank
  @Size(max = 80)
  public String nombre;

  @NotBlank
  @Email
  @Size(max = 120)
  public String correoElectronico;

  @NotBlank
  @Size(min = 8, max = 100)
  public String contrasena;

  @Size(max = 20)
  public String telefono;

  @NotNull
  public Long idTarifa;

  @Size(max = 200)
  public String direccion;

  @Size(max = 80)
  public String ciudad;

  @Size(max = 10)
  public String codigoPostal;

  @NotBlank
  @Size(max = 120)
  public String paymentToken;
}
