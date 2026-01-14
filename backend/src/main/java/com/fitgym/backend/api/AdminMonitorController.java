package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.MonitorAdminRequest;
import com.fitgym.backend.api.dto.MonitorAdminResponse;
import com.fitgym.backend.api.dto.SocioSession;
import com.fitgym.backend.service.MonitorAdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/monitores")
public class AdminMonitorController {

  private final MonitorAdminService monitorAdminService;

  private static final String SESSION_SOCIO_KEY = "socioLogin";
  private static final String ADMIN_EMAIL = "admin@gmail.com";

  public AdminMonitorController(MonitorAdminService monitorAdminService) {
    this.monitorAdminService = monitorAdminService;
  }

  @GetMapping
  public List<MonitorAdminResponse> listar(HttpServletRequest request) {
    requireAdmin(request);
    return monitorAdminService.listar();
  }

  @PostMapping
  public MonitorAdminResponse crear(@Valid @RequestBody MonitorAdminRequest req, HttpServletRequest request) {
    requireAdmin(request);
    return monitorAdminService.crear(req);
  }

  @PutMapping("/{id}")
  public MonitorAdminResponse editar(@PathVariable Long id, @Valid @RequestBody MonitorAdminRequest req, HttpServletRequest request) {
    requireAdmin(request);
    return monitorAdminService.editar(id, req);
  }

  private void requireAdmin(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay sesion activa.");

    Object value = session.getAttribute(SESSION_SOCIO_KEY);
    if (!(value instanceof SocioSession socioSession)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay sesion activa.");
    }
    if (!ADMIN_EMAIL.equalsIgnoreCase(socioSession.toLoginResponse().correoElectronico())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado.");
    }
  }
}
