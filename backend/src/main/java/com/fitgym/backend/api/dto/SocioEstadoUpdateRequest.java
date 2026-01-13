package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.NotBlank;

public class SocioEstadoUpdateRequest {
  @NotBlank
  public String estado; // "ACTIVO" o "INACTIVO"
}
