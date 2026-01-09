package com.fitgym.backend.service.tpvv.dto;

public record TpvvPaymentVerifyResponse(
    String status,
    String failureReason
) {}
