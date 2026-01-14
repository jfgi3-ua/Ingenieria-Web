package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MonitorAdminRequest {

  @NotBlank
  @Size(max = 80)
  public String nombre;

  @NotBlank
  @Size(max = 20)
  public String dni;

  @NotBlank
  @Email
  @Size(max = 120)
  public String correoElectronico;

  // para admin: permitimos setear/actualizar contraseña del monitor
  @NotBlank
  @Size(min = 6, max = 120)
  public String contrasena;

  @NotBlank
  @Size(max = 20)
  public String telefono;

  @NotBlank
  @Size(max = 80)
  public String ciudad;

  @NotBlank
  @Size(max = 200)
  public String direccion;

  @NotBlank
  @Size(max = 10)
  public String codigoPostal;
}
