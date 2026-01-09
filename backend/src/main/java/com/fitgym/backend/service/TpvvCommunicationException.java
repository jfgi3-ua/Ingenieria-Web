package com.fitgym.backend.service;

/**
 * Error al comunicarse con TPVV (timeout, respuesta invalida o error HTTP).
 */
public class TpvvCommunicationException extends BusinessException {
  public TpvvCommunicationException(String message) { super(message); }
}
