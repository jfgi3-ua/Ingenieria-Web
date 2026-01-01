package com.fitgym.backend.service;

/**
 * Se lanza cuando el pago existe pero aun no esta completado.
 */
public class PagoRegistroNoCompletadoException extends BusinessException {
  public PagoRegistroNoCompletadoException(String message) { super(message); }
}
