package com.fitgym.backend.repo;

import com.fitgym.backend.domain.Actividad;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadRepository extends JpaRepository<Actividad, Long>{
    Optional<Actividad> findByNombreIgnoreCase(String nombre);
}
