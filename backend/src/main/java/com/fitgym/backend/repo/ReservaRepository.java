package com.fitgym.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitgym.backend.domain.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long>{
    
}
