package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Clase que representa la solicitud de registro de un socio en el sistema.
 * Contiene la información necesaria para crear un nuevo socio.
 *
 * Atributos:
 *
 * @param nombre El nombre del socio. No puede estar vacío y tiene un máximo de 80 caracteres.
 * @param correoElectronico La dirección de correo electrónico del socio. No puede estar vacío,
 *   debe ser un correo electrónico válido y tiene un máximo de 120 caracteres.
 * @param contrasena La contraseña del socio. No puede estar vacía y debe tener entre 8 y 100 caracteres.
 * @param telefono El número de teléfono del socio. Puede ser nulo y tiene un máximo de 20 caracteres.
 * @param idTarifa El identificador de la tarifa asociada al socio. No puede ser nulo.
 * @param direccion La dirección del socio. Puede ser nula y tiene un máximo de 200 caracteres.
 * @param ciudad La ciudad del socio. Puede ser nula y tiene un máximo de 80 caracteres.
 * @param codigoPostal El código postal del socio. Puede ser nulo y tiene un máximo de 10 caracteres.
 */
public class SocioRegistroRequest {

  @NotBlank
  @Size(max = 80)
  public String nombre;

  @NotBlank
  @Email
  @Size(max = 120)
  public String correoElectronico;

  @NotBlank
  @Size(min = 8, max = 100)
  public String contrasena;

  @Size(max = 20)
  public String telefono;

  @NotNull
  public Long idTarifa;

  @Size(max = 200)
  public String direccion;

  @Size(max = 80)
  public String ciudad;

  @Size(max = 10)
  public String codigoPostal;
}
