package com.fitgym.backend.api.dto;

public record MonitorAdminResponse(
    Long id,
    String nombre,
    String dni,
    String correoElectronico,
    String telefono,
    String ciudad,
    String direccion,
    String codigoPostal
) {}
