package com.fitgym.backend.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MembresiaResponse(
        Long idTarifa,
        String tarifaNombre,
        BigDecimal precioMensual,
        String estadoPago,
        OffsetDateTime fechaInicio,
        OffsetDateTime proximaRenovacion,
        OffsetDateTime ultimoPago
) {}