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

import java.util.List;

@RestController
@RequestMapping("/api/admin/socios")
public class AdminSocioController {

  private final SocioService socioService;

  public AdminSocioController(SocioService socioService) {
    this.socioService = socioService;
  }

  @GetMapping
  public ResponseEntity<List<SocioAdminResponse>> listar() {
    List<SocioAdminResponse> res = socioService.listarTodos().stream()
        .map(AdminSocioController::toAdminResponse)
        .toList();
    return ResponseEntity.ok(res);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SocioAdminResponse> detalle(@PathVariable Long id) {
    Socio socio = socioService.obtenerPorId(id);
    return ResponseEntity.ok(toAdminResponse(socio));
  }

  @PutMapping("/{id}")
  public ResponseEntity<SocioAdminResponse> actualizar(@PathVariable Long id, @Valid @RequestBody SocioAdminUpdateRequest req) {
    Socio socio = socioService.actualizarComoAdmin(id, req);
    return ResponseEntity.ok(toAdminResponse(socio));
  }

  @PatchMapping("/{id}/estado")
  public ResponseEntity<SocioAdminResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody SocioEstadoUpdateRequest req) {
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
}
