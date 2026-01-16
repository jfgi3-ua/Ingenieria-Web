package com.fitgym.backend.api.dto;

public record MonederoRecargaResponse(
        String paymentUrl,
        String token
) {}