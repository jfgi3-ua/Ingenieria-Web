package com.fitgym.backend.repo;

import com.fitgym.backend.domain.Reserva;
import com.fitgym.backend.domain.ReservaId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, ReservaId>{
    @Query("""
      select r
      from Reserva r
      where r.id.idSocio = :idSocio
      order by r.fecha desc
    """)
    List<Reserva> findBySocioOrderByFechaDesc(@Param("idSocio") Long idSocio);
}
