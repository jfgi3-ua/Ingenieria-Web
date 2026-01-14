package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.SocioAdminResponse;
import com.fitgym.backend.api.dto.SocioAdminUpdateRequest;
import com.fitgym.backend.api.dto.SocioEstadoUpdateRequest;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.domain.SocioEstado;
import com.fitgym.backend.service.SocioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitgym.backend.api.dto.SocioSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@RestController
@RequestMapping("/api/admin/socios")
public class AdminSocioController {

  private final SocioService socioService;

  public AdminSocioController(SocioService socioService) {
    this.socioService = socioService;
  }

    @GetMapping
  public ResponseEntity<List<SocioAdminResponse>> listar(HttpServletRequest request) {
    requireAdmin(request);

    List<SocioAdminResponse> res = socioService.listarTodos().stream()
        .map(AdminSocioController::toAdminResponse)
        .toList();
    return ResponseEntity.ok(res);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SocioAdminResponse> detalle(@PathVariable Long id, HttpServletRequest request) {
    requireAdmin(request);

    Socio socio = socioService.obtenerPorId(id);
    return ResponseEntity.ok(toAdminResponse(socio));
  }

  @PutMapping("/{id}")
  public ResponseEntity<SocioAdminResponse> actualizar(
      @PathVariable Long id,
      @Valid @RequestBody SocioAdminUpdateRequest req,
      HttpServletRequest request
  ) {
    requireAdmin(request);

    Socio socio = socioService.actualizarComoAdmin(id, req);
    return ResponseEntity.ok(toAdminResponse(socio));
  }

  @PatchMapping("/{id}/estado")
  public ResponseEntity<SocioAdminResponse> cambiarEstado(
      @PathVariable Long id,
      @Valid @RequestBody SocioEstadoUpdateRequest req,
      HttpServletRequest request
  ) {
    requireAdmin(request);

    SocioEstado estado = SocioEstado.valueOf(req.estado.trim().toUpperCase());
    Socio socio = socioService.cambiarEstado(id, estado);
    return ResponseEntity.ok(toAdminResponse(socio));
  }


  private static SocioAdminResponse toAdminResponse(Socio s) {
    return new SocioAdminResponse(
        s.getId(),
        s.getNombre(),
        s.getCorreoElectronico(),
        s.getTelefono(),
        s.getEstado().name(),
        s.getTarifa().getId(),
        s.getTarifa().getNombre(),
        s.isPagoDomiciliado(),
        s.getSaldoMonedero(),
        s.getClasesGratis(),
        s.getDireccion(),
        s.getCiudad(),
        s.getCodigoPostal()
    );
  }

  private static final String SESSION_SOCIO_KEY = "socioLogin";
private static final String ADMIN_EMAIL = "admin@gmail.com";

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
