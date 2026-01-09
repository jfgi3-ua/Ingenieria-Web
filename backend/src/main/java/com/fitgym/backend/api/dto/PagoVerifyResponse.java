package com.fitgym.backend.api.dto;

/**
 * Respuesta con el estado actualizado del pago.
 */
public class PagoVerifyResponse {
  public String status;
  public String failureReason;

  public PagoVerifyResponse(String status, String failureReason) {
    this.status = status;
    this.failureReason = failureReason;
  }
}
