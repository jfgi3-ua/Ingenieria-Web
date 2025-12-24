package com.fitgym.backend.service;

/**
 * Excepcion de negocio para indicar email duplicado durante el registro.
 */
public class DuplicateEmailException extends BusinessException {
  public DuplicateEmailException(String message) {
    super(message);
  }
}
