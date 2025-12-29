package com.fitgym.backend.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "actividad")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name= "hora_ini", nullable = false)
    private LocalTime horaIni;

    @Column(name= "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "precio_extra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioExtra = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Integer plazas = 0;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_monitor", nullable = false)
    private Monitor monitor;
    
    /*
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_actividad", nullable = false)
    private TipoActividad tipoActividad;*/

    // Getters y Setters
    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalTime getHoraIni() { return horaIni; }
    public void setHoraIni(LocalTime horaIni) { this.horaIni = horaIni; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public BigDecimal getPrecioExtra() { return precioExtra; }
    public void setPrecioExtra(BigDecimal precioExtra) { this.precioExtra = precioExtra; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Integer getPlazas() { return plazas; }
    public void setPlazas(Integer plazas) { this.plazas = plazas; }

    
    public Monitor getMonitor() { return monitor; }
    public void setMonitor(Monitor monitor) { this.monitor = monitor; }

    /*
    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }

    public TipoActividad getTipoActividad() { return tipoActividad; }
    public void setTipoActividad(TipoActividad tipoActividad) { this.tipoActividad = tipoActividad; }
    */
}
