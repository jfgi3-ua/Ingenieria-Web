package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.NotNull;

public record PreferenciasUpdateRequest(
        boolean notificaciones,
        boolean recordatorios,
        boolean comunicaciones
) {}