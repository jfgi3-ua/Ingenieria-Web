package com.fitgym.backend.api.dto;

/**
 * Respuesta simple para comprobar si un correo ya existe.
 */
public class EmailExistsResponse {
  public boolean exists;

  public EmailExistsResponse(boolean exists) {
    this.exists = exists;
  }
}
