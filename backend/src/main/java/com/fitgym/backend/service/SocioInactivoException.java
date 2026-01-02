package com.fitgym.backend.service;

/**
 * Excepcion lanzada cuando un socio intenta iniciar sesion estando inactivo.
 *
 * Se utiliza para devolver un 403 (Forbidden) en el endpoint de login.
 */
public class SocioInactivoException extends RuntimeException {
  public SocioInactivoException(String message) {
    super(message);
  }
}
