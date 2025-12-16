package com.fitgym.backend.api.dto;

/**
 * Clase de respuesta que representa la información de un socio en el sistema.
 *
 * Esta clase es un registro inmutable que contiene los detalles de un socio,
 * incluyendo su identificación, nombre, correo electrónico, estado,
 * identificación de la tarifa y el nombre de la tarifa asociada.
 *
 * Atributos:
 *
 * @param id: Identificador único del socio. Este valor es de tipo Long.
 * @param nombre: Nombre completo del socio. Este valor es de tipo String.
 * @param correoElectronico: Dirección de correo electrónico del socio. Este valor es de tipo String.
 * @param estado: Estado actual del socio (por ejemplo, activo, inactivo). Este valor es de tipo String.
 * @param idTarifa: Identificador de la tarifa asociada al socio. Este valor es de tipo Long.
 * @param tarifaNombre: Nombre de la tarifa asociada al socio. Este valor es de tipo String.
 *
 * Esta clase se utiliza para transferir datos entre las capas de la aplicación,
 * facilitando la comunicación y el manejo de la información relacionada con los socios.
 */
public record SocioResponse(
    Long id,
    String nombre,
    String correoElectronico,
    String estado,
    Long idTarifa,
    String tarifaNombre
) {}
