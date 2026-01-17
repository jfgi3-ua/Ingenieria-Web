package com.fitgym.backend.api.dto;

import java.math.BigDecimal;

public record ReservaCancelResponse(
        String estado,
        BigDecimal reembolso,
        BigDecimal saldoMonedero
) {}