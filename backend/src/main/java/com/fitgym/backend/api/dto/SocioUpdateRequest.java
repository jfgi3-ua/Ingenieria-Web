package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.NotBlank;

public record SocioUpdateRequest(
        @NotBlank String nombre,
        String telefono,
        @NotBlank String direccion,
        @NotBlank String ciudad,
        @NotBlank String codigoPostal
) {}