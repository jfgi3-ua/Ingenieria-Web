package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.IdNombreResponse;
import com.fitgym.backend.api.dto.SalaLookupResponse;
import com.fitgym.backend.api.dto.SocioSession;
import com.fitgym.backend.repo.MonitorRepository;
import com.fitgym.backend.repo.SalaRepository;
import com.fitgym.backend.repo.TipoActividadRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lookups")
public class AdminLookupsController {

  private final MonitorRepository monitorRepository;
  private final SalaRepository salaRepository;
  private final TipoActividadRepository tipoActividadRepository;

  private static final String SESSION_SOCIO_KEY = "socioLogin";
  private static final String ADMIN_EMAIL = "admin@gmail.com";

  public AdminLookupsController(
      MonitorRepository monitorRepository,
      SalaRepository salaRepository,
      TipoActividadRepository tipoActividadRepository
  ) {
    this.monitorRepository = monitorRepository;
    this.salaRepository = salaRepository;
    this.tipoActividadRepository = tipoActividadRepository;
  }

  @GetMapping("/monitores")
  public List<IdNombreResponse> monitores(HttpServletRequest request) {
    requireAdmin(request);
    return monitorRepository.findAll().stream()
        .map(m -> new IdNombreResponse(m.getId(), m.getNombre()))
        .toList();
  }

  @GetMapping("/salas")
  public List<SalaLookupResponse> salas(HttpServletRequest request) {
    requireAdmin(request);
    return salaRepository.findAll().stream()
        .map(s -> new SalaLookupResponse(s.getId(), s.getDescripcion(), s.getFoto()))
        .toList();
  }

  @GetMapping("/tipos-actividad")
  public List<IdNombreResponse> tipos(HttpServletRequest request) {
    requireAdmin(request);
    return tipoActividadRepository.findAll().stream()
        .map(t -> new IdNombreResponse(t.getId(), t.getNombre()))
        .toList();
  }

  private void requireAdmin(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay sesion activa.");
    }
    Object value = session.getAttribute(SESSION_SOCIO_KEY);
    if (!(value instanceof SocioSession socioSession)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay sesion activa.");
    }
    if (!ADMIN_EMAIL.equalsIgnoreCase(socioSession.toLoginResponse().correoElectronico())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado.");
    }
  }
}
