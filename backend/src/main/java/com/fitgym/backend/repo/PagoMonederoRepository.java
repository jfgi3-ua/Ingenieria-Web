package com.fitgym.backend.repo;

import com.fitgym.backend.domain.PagoMonedero;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoMonederoRepository extends JpaRepository<PagoMonedero, Long> {
    Optional<PagoMonedero> findByToken(String token);
}