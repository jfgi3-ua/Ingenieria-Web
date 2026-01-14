package com.fitgym.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "sala")
public class Sala {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String descripcion;

  @Column(nullable = false)
  private Integer aforo;

  @Column(length = 300)
  private String foto;

  public Long getId() { return id; }

  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

  public Integer getAforo() { return aforo; }
  public void setAforo(Integer aforo) { this.aforo = aforo; }

  public String getFoto() { return foto; }
  public void setFoto(String foto) { this.foto = foto; }
}
