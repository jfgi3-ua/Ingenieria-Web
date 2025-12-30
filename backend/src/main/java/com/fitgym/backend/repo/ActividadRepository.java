package com.fitgym.backend.repo;

import com.fitgym.backend.domain.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadRepository extends JpaRepository<Actividad, Long>{
    
}
