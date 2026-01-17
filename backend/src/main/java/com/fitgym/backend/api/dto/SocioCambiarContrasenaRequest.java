package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.NotBlank;

public record SocioCambiarContrasenaRequest(
        @NotBlank String contrasenaActual,
        @NotBlank String nuevaContrasena
) {}