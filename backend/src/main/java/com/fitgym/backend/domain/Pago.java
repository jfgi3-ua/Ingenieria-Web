package com.fitgym.backend.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pago",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_pago_reserva",
            columnNames = {"id_socio", "id_actividad"}
        )
    }
)
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "id_socio", nullable = false)
    private Long idSocio;

    @Column(name = "id_actividad", nullable = false)
    private Long idActividad;

    @Column(name = "fecha_pago", nullable = false)
    private Instant fechaPago;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "resultado_pago", nullable = false, columnDefinition = "pago_resultado")
    private PagoResultado resultadoPago;

    @PrePersist
    void onCreate() {
        if (fechaPago == null) {
            fechaPago = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(Long idSocio) {
        this.idSocio = idSocio;
    }

    public Long getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Long idActividad) {
        this.idActividad = idActividad;
    }

    public Instant getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Instant fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public PagoResultado getResultadoPago() {
        return resultadoPago;
    }

    public void setResultadoPago(PagoResultado resultadoPago) {
        this.resultadoPago = resultadoPago;
    }
}

