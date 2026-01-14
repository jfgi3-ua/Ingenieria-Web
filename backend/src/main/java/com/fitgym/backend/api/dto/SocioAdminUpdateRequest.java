package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SocioAdminUpdateRequest {

  @NotBlank
  @Size(max = 80)
  public String nombre;

  @NotBlank
  @Email
  @Size(max = 120)
  public String correoElectronico;

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

  public Boolean pagoDomiciliado;
}
