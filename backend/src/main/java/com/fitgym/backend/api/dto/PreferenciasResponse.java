package com.fitgym.backend.api.dto;

public record PreferenciasResponse(
        boolean notificaciones,
        boolean recordatorios,
        boolean comunicaciones
) {}