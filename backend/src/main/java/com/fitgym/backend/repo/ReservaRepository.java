package com.fitgym.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitgym.backend.domain.Reserva;
import com.fitgym.backend.domain.ReservaId;

public interface ReservaRepository extends JpaRepository<Reserva, ReservaId>{
    
}
