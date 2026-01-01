package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request para iniciar un pago de registro desde el frontend.
 */
public class PagoInitRequest {
  @NotNull
  public Long idTarifa;
}
