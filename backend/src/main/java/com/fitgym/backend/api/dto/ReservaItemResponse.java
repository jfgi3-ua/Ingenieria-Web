package com.fitgym.backend.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaItemResponse(
        Long idActividad,
        String actividadNombre,
        LocalDate fecha,
        LocalTime horaIni,
        LocalTime horaFin,
        String estado,
        BigDecimal precioPagado
) {}