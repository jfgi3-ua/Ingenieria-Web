package com.fitgym.backend.repo;

import com.fitgym.backend.domain.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Interfaz SocioRepository que extiende JpaRepository para la entidad Socio.
 * Proporciona métodos para realizar operaciones de acceso a datos en la base de datos.
 *
 * Métodos disponibles:
 *
 * - existsByCorreoElectronicoIgnoreCase(String correoElectronico):
 *   Verifica si existe un socio en la base de datos con el correo electrónico proporcionado,
 *   ignorando mayúsculas y minúsculas.
 *
 * - findByCorreoElectronico(String correoElectronico):
 *   Busca y retorna un socio por su correo electrónico. Devuelve un Optional que contiene el socio si se encuentra,
 *   o está vacío si no existe.
 *
 * Esta interfaz permite interactuar con la tabla de socios de manera sencilla y eficiente,
 * aprovechando las funcionalidades que ofrece Spring Data JPA.
 */
public interface SocioRepository extends JpaRepository<Socio, Long> {
  boolean existsByCorreoElectronicoIgnoreCase(String correoElectronico);
  Optional<Socio> findByCorreoElectronico(String correoElectronico);
}
