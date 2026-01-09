package com.fitgym.backend.domain;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "reserva")
public class Reserva {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_socio", nullable = false)
    private Socio socio;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad", nullable = false)
    private Actividad actividad;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private ReservaEstado estado;

    //Getters y Setters
    public Socio getSocio() { return socio; }
    public void setSocio(Socio socio) { this.socio = socio; }

    public Actividad getActividad() { return actividad; }
    public void setActividad(Actividad actividad) { this.actividad = actividad; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public ReservaEstado getEstado() { return estado; }
    public void setEstado(ReservaEstado estado) { this.estado = estado; }
}
