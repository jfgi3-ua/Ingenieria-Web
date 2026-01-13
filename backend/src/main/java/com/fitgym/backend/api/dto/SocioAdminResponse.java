package com.fitgym.backend.api.dto;

import java.math.BigDecimal;

public record SocioAdminResponse(
    Long id,
    String nombre,
    String correoElectronico,
    String telefono,
    String estado,
    Long idTarifa,
    String tarifaNombre,
    Boolean pagoDomiciliado,
    BigDecimal saldoMonedero,
    Integer clasesGratis,
    String direccion,
    String ciudad,
    String codigoPostal
) {}
