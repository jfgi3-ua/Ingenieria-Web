package com.fitgym.backend.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "reserva")
public class Reserva {
    @EmbeddedId
    private ReservaId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("idSocio")
    @JoinColumn(name = "id_socio", nullable = false)
    private Socio socio;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("idActividad")
    @JoinColumn(name = "id_actividad", nullable = false)
    private Actividad actividad;

    @Column(name = "fecha_alta", nullable = false)
    private OffsetDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservaEstado estado;

    //Getters y Setters
    public ReservaId getId() { return id; }
    public void setId(ReservaId id) { this.id = id; }
    
    public Socio getSocio() { return socio; }
    public void setSocio(Socio socio) { this.socio = socio; }

    public Actividad getActividad() { return actividad; }
    public void setActividad(Actividad actividad) { this.actividad = actividad; }

    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }

    public ReservaEstado getEstado() { return estado; }
    public void setEstado(ReservaEstado estado) { this.estado = estado; }
}
