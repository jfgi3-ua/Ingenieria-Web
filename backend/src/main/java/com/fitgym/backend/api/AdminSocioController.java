package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.AdminSocioResponse;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.service.SocioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/socios")
public class AdminSocioController {

  private final SocioService socioService;

  public AdminSocioController(SocioService socioService) {
    this.socioService = socioService;
  }

  @GetMapping
  public List<AdminSocioResponse> listarSocios() {
    return socioService.listarTodos()
      .stream()
      .map(this::toResponse)
      .collect(Collectors.toList());
  }

  private AdminSocioResponse toResponse(Socio s) {
    AdminSocioResponse r = new AdminSocioResponse();
    r.setId(s.getId());
    r.setNombre(s.getNombre());
    r.setCorreoElectronico(s.getCorreoElectronico());
    r.setTelefono(s.getTelefono());
    r.setEstado(s.getEstado().name());
    r.setPagoDomiciliado(s.isPagoDomiciliado());
    r.setSaldoMonedero(s.getSaldoMonedero());
    r.setTarifaId(s.getTarifa().getId());
    r.setTarifaNombre(s.getTarifa().getNombre());
    return r;
  }
}
