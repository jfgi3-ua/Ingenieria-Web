package com.fitgym.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tarifa")
public class Tarifa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  private String nombre;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal cuota;

  private String descripcion;

  @Column(name = "clases_gratis_mes", nullable = false)
  private Integer clasesGratisMes;

  public Long getId() { return id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public BigDecimal getCuota() { return cuota; }
  public void setCuota(BigDecimal cuota) { this.cuota = cuota; }

  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

  public Integer getClasesGratisMes() { return clasesGratisMes; }
  public void setClasesGratisMes(Integer clasesGratisMes) { this.clasesGratisMes = clasesGratisMes; }
}
