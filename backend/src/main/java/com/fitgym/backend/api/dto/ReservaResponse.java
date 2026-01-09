package com.fitgym.backend.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.fitgym.backend.domain.ReservaEstado;

/**
 * Clase de respuesta que representa la información de una reserva en el sistema.
 *
 * Esta clase es un registro inmutable que contiene los detalles de una reserva,
 * incluyendo su socio, actividad, fecha de alta y estado,
 *
 * Atributos:
 *
 * @param idSocio: Identificador único del socio de la reserva. Este valor es de tipo Long.
 * @param idActividad: Identificador único de la actividad de la reserva. Este valor es de tipo Long.
 * @param fecha: Dia de la actividad. Este valor es de tipo Date.
 * @param estado: Estado de la transaccion. Es boolean para tener 2 estados: CONFIRMADO(true)
 * 
 * Esta clase se utiliza para transferir datos entre las capas de la aplicación,
 * facilitando la comunicación y el manejo de la información relacionada con los socios.
 */

public record ReservaResponse(
    Long idSocio,
    Long idActividad,
    OffsetDateTime fecha,
    ReservaEstado estado
) {}
