package com.fitgym.backend.api.dto;

public record MonederoVerifyResponse(
        String estado,
        String failureReason
) {}