package com.fitgym.backend.api.dto;

import java.math.BigDecimal;

public class AdminSocioResponse {

  private Long id;
  private String nombre;
  private String correoElectronico;
  private String telefono;

  private String estado;

  private Long tarifaId;
  private String tarifaNombre;

  private boolean pagoDomiciliado;
  private BigDecimal saldoMonedero;

  // getters & setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getCorreoElectronico() { return correoElectronico; }
  public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

  public String getTelefono() { return telefono; }
  public void setTelefono(String telefono) { this.telefono = telefono; }

  public String getEstado() { return estado; }
  public void setEstado(String estado) { this.estado = estado; }

  public Long getTarifaId() { return tarifaId; }
  public void setTarifaId(Long tarifaId) { this.tarifaId = tarifaId; }

  public String getTarifaNombre() { return tarifaNombre; }
  public void setTarifaNombre(String tarifaNombre) { this.tarifaNombre = tarifaNombre; }

  public boolean isPagoDomiciliado() { return pagoDomiciliado; }
  public void setPagoDomiciliado(boolean pagoDomiciliado) { this.pagoDomiciliado = pagoDomiciliado; }

  public BigDecimal getSaldoMonedero() { return saldoMonedero; }
  public void setSaldoMonedero(BigDecimal saldoMonedero) { this.saldoMonedero = saldoMonedero; }
}
