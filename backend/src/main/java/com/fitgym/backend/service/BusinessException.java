package com.fitgym.backend.service;

/**
 * Excepción de negocio personalizada que se lanza cuando ocurre un error
 * relacionado con la lógica de negocio de la aplicación (para mensajes claros al frontend).
 *
 * Extiende de {@link RuntimeException} para permitir que sea una excepción
 * no verificada, facilitando su propagación a través de la pila de llamadas
 * sin necesidad de declaración explícita en la firma del método.
 */
public class BusinessException extends RuntimeException {
  public BusinessException(String message) { super(message); }
}
