package com.fitgym.backend.service;

/**
 * Se lanza cuando no existe una transaccion de pago con el token indicado.
 */
public class PagoRegistroNotFoundException extends BusinessException {
  public PagoRegistroNotFoundException(String message) { super(message); }
}
