package com.fitgym.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_actividad")
public class TipoActividad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String nombre;

  @Column(nullable = false, length = 500)
  private String descripcion;

  // NUEVO: foto del tipo (para pintar las cards)
  // Será una URL o un nombre de recurso (string).
 
  public Long getId() { return id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }


}
