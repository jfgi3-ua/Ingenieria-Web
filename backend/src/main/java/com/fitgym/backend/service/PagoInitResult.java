package com.fitgym.backend.service;

/**
 * Resultado interno del inicio de pago (token y URL de pago).
 */
public record PagoInitResult(String paymentUrl, String token) {}
