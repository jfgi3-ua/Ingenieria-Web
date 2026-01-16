package com.fitgym.backend.repo;

import com.fitgym.backend.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    @Query("""
    SELECT p FROM Pago p
    WHERE p.idSocio = :idSocio
      AND (p.idActividad IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')))
    ORDER BY p.fechaPago DESC
  """)
    Optional<Pago> findUltimoPagoMembresia(@Param("idSocio") Long idSocio, @Param("keyword") String keyword);
}



