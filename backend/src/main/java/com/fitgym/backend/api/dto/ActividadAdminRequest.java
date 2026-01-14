package com.fitgym.backend.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActividadAdminRequest {

  @NotNull
  @Size(min = 1, max = 120)
  public String nombre;

  @NotNull
  public LocalTime horaIni;

  @NotNull
  public LocalTime horaFin;

  @NotNull
  public BigDecimal precioExtra;

  @NotNull
  public LocalDate fecha;

  @NotNull
  @Min(1)
  public Integer plazas;

  @NotNull
  public Long idMonitor;

  @NotNull
  public Long idSala;

  @NotNull
  public Long idTipoActividad;
}
