package com.fitgym.backend.service;

/**
 * Excepcion lanzada cuando las credenciales de acceso son invalidas.
 *
 * Se utiliza para devolver un 401 (Unauthorized) en el endpoint de login.
 */
public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
