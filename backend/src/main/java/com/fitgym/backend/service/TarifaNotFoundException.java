package com.fitgym.backend.service;

/**
 * Excepcion de negocio para indicar que la tarifa no existe.
 */
public class TarifaNotFoundException extends BusinessException {
  public TarifaNotFoundException(String message) {
    super(message);
  }
}
