package com.fitgym.backend.repo;

import com.fitgym.backend.domain.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ActividadRepository extends JpaRepository<Actividad, Long>{
    
}
