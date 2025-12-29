package com.fitgym.backend.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase de respuesta que representa la información de una actividad en el sistema.
 *
 * Esta clase es un registro inmutable que contiene los detalles de una actividad,
 * incluyendo su identificación, nombre, hora inicio, hora fin,
 * precio extra, la fecha en que se realiza y las plazas que tiene.
 *
 * Atributos:
 *
 * @param id: Identificador único de la actividad. Este valor es de tipo Long.
 * @param nombre: Nombre de la actividad. Este valor es de tipo String.
 * @param horaIni: Hora de inicio de la actividad. Este valor es de tipo LocalDate.
 * @param horaFin: Hora de finalizacion de la actividad. Este valor es de tipo LocalDate.
 * @param precioExtra: Precio extra si no se incluye en la tarifa. Este valor es de tipo BigDecimal.
 * @param fecha: Dia de la actividad. Este valor es de tipo Date.
 * @param plazas: Plazas totales en la activida. Este valor es de tipo BigInteger
 * @param disponibles: Plazas disponibles en la activida. Este valor es de tipo BigInteger
 * @param monitor: Nombre del monitor que da la clase. Este valor es de tipo String.
 * 
 * Esta clase se utiliza para transferir datos entre las capas de la aplicación,
 * facilitando la comunicación y el manejo de la información relacionada con los socios.
 */

public record ActividadResponse(
    Long id,
    String nombre,
    LocalTime horaIni,
    LocalTime horaFin,
    BigDecimal precioExtra,
    LocalDate fecha,
    Integer plazas,
    Integer disponibles,
    String monitor
) {}
