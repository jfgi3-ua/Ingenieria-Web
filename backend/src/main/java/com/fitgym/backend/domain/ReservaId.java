package com.fitgym.backend.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ReservaId implements Serializable {

    @Column(name = "id_socio")
    private Long idSocio;

    @Column(name = "id_actividad")
    private Long idActividad;

    public ReservaId() {}

    public ReservaId(Long idSocio, Long idActividad) {
        this.idSocio = idSocio;
        this.idActividad = idActividad;
    }

    // Getters y setters
    public Long setIdSocio() { return idSocio; }
    public void getIdSocio(Long idSocio) { this.idSocio = idSocio; }

    public Long setIdActividad() { return idActividad; }
    public void getIdActividad(Long idActividad) { this.idActividad = idActividad; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservaId that)) return false;
        return Objects.equals(idSocio, that.idSocio)
            && Objects.equals(idActividad, that.idActividad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSocio, idActividad);
    }
}

