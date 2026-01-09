package com.fitgym.backend.service.tpvv.dto;

import java.math.BigDecimal;

public record TpvvPaymentInitRequest(
    BigDecimal amount,
    String callbackUrl,
    String externalReference
) {}
