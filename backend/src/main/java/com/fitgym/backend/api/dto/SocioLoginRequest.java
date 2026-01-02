package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para el inicio de sesion de un socio.
 *
 * Este objeto representa el payload esperado por el endpoint de login
 * (por ejemplo, {@code POST /api/socios/login}). Contiene el correo
 * electronico y la contrasena en texto plano que el usuario introduce
 * en el formulario. La contrasena no se persiste ni se devuelve en ninguna
 * respuesta; solo se usa para validar la credencial.
 *
 * Validaciones aplicadas:
 * - {@code correoElectronico}: obligatorio, formato email, max 120 caracteres.
 * - {@code contrasena}: obligatoria, entre 8 y 100 caracteres.
 */
public class SocioLoginRequest {

  @NotBlank
  @Email
  @Size(max = 120)
  public String correoElectronico;

  @NotBlank
  @Size(min = 8, max = 100)
  public String contrasena;
}
