package com.fitgym.backend.api.dto;

/**
 * Respuesta para iniciar el pago con TPVV.
 */
public class PagoInitResponse {
  public String paymentUrl;
  public String token;

  public PagoInitResponse(String paymentUrl, String token) {
    this.paymentUrl = paymentUrl;
    this.token = token;
  }
}
