package com.fitgym.backend.service.tpvv.dto;

public record TpvvPaymentInitResponse(
    String paymentUrl,
    String token
) {}
