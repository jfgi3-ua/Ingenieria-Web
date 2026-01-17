package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record MonederoRecargaRequest(
        @NotNull @Positive BigDecimal importe
) {}