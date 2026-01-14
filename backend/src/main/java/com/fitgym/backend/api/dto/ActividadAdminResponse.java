package com.fitgym.backend.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ActividadAdminResponse(
    Long id,
    String nombre,
    LocalTime horaIni,
    LocalTime horaFin,
    BigDecimal precioExtra,
    LocalDate fecha,
    Integer plazas,
    Integer disponibles,
    Long idMonitor,
    String monitorNombre,
    Long idSala,
    String salaDescripcion,
    String salaFoto,
    Long idTipoActividad,
    String tipoActividadNombre
) {}
