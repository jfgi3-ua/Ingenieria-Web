package com.fitgym.backend.repo;

import com.fitgym.backend.domain.PagoRegistro;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRegistroRepository extends JpaRepository<PagoRegistro, Long> {
  Optional<PagoRegistro> findByToken(String token);
}
