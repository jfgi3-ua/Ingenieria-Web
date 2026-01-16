package com.fitgym.backend.service.tpvv;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion de acceso al TPVV (base URL, API key y callback).
 */
@ConfigurationProperties(prefix = "tpvv")
public record TpvvProperties(
    String baseUrl,
    String apiKey,
    String callbackUrlRegistro,
    String callbackUrlMonedero
) {}
