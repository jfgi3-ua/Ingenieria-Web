package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.ActividadAdminRequest;
import com.fitgym.backend.api.dto.ActividadAdminResponse;
import com.fitgym.backend.api.dto.SocioSession;
import com.fitgym.backend.service.ActividadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/actividades")
public class AdminActividadController {

  private final ActividadService actividadService;

  private static final String SESSION_SOCIO_KEY = "socioLogin";
  private static final String ADMIN_EMAIL = "admin@gmail.com";

  public AdminActividadController(ActividadService actividadService) {
    this.actividadService = actividadService;
  }

  @GetMapping
  public ResponseEntity<List<ActividadAdminResponse>> listar(HttpServletRequest request) {
    requireAdmin(request);
    return ResponseEntity.ok(actividadService.adminListar());
  }

  @PostMapping
  public ResponseEntity<ActividadAdminResponse> crear(@Valid @RequestBody ActividadAdminRequest req, HttpServletRequest request) {
    requireAdmin(request);
    return ResponseEntity.ok(actividadService.adminCrear(req));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ActividadAdminResponse> editar(
      @PathVariable Long id,
      @Valid @RequestBody ActividadAdminRequest req,
      HttpServletRequest request
  ) {
    requireAdmin(request);
    return ResponseEntity.ok(actividadService.adminEditar(id, req));
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
