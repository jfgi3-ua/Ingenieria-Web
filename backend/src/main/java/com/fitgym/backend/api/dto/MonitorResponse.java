package com.fitgym.backend.api.dto;

/**
 * Clase de respuesta que representa la información de un monitor en el sistema.
 *
 * Esta clase es un registro inmutable que contiene los detalles de un monitor,
 * incluyendo su identificación, nombre, dni, correo, contraseña, telefono
 * ciudad, direccion y codigo postal.
 *
 * Atributos:
 *
 * @param id: Identificador único de la actividad. Este valor es de tipo Long.
 * @param nombre: Nombre de la actividad. Este valor es de tipo String.
 * @param dni: DNI del monitor. Este valor es de tipo String.
 * @param correoElectronico: Correo electronico del monitor. Este valor es de tipo String.
 * @param contrasenya: Contraseña del monitor hasheada. Este valor es de tipo String.
 * @param telefono: Numero telefonico del monitor. Este valor es de tipo String.
 * @param ciudad: Ciudad de residencia del monitor. Este valor es de tipo String.
 * @param direccion: Direccion del domicilio del monitor. Este valor es de tipo String.
 * @param codigoPostal: Codigo postal de la ciudad/barrio donde vive el monitor. Este valor es de tipo String.
 * 
 * Esta clase se utiliza para transferir datos entre las capas de la aplicación,
 * facilitando la comunicación y el manejo de la información relacionada con los socios.
 */

public record MonitorResponse(
    Long id,
    String nombre,
    String dni,
    String correoElectronico,
    String contrasenya,
    String telefono,
    String ciudad,
    String direccion,
    String codigoPostal
) {}
