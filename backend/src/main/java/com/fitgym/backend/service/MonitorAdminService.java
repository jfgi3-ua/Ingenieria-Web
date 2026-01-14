package com.fitgym.backend.service;

import com.fitgym.backend.api.dto.MonitorAdminRequest;
import com.fitgym.backend.api.dto.MonitorAdminResponse;
import com.fitgym.backend.domain.Monitor;
import com.fitgym.backend.repo.MonitorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MonitorAdminService {

  private final MonitorRepository monitorRepository;
  private final PasswordEncoder passwordEncoder;

  public MonitorAdminService(MonitorRepository monitorRepository, PasswordEncoder passwordEncoder) {
    this.monitorRepository = monitorRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional(readOnly = true)
  public List<MonitorAdminResponse> listar() {
    return monitorRepository.findAll().stream()
        .map(MonitorAdminService::toResponse)
        .toList();
  }

  @Transactional
  public MonitorAdminResponse crear(MonitorAdminRequest req) {
    Monitor m = new Monitor();
    aplicar(m, req);
    Monitor saved = monitorRepository.save(m);
    return toResponse(saved);
  }

  @Transactional
  public MonitorAdminResponse editar(Long id, MonitorAdminRequest req) {
    Monitor m = monitorRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Monitor no encontrado: " + id));
    aplicar(m, req);
    Monitor saved = monitorRepository.save(m);
    return toResponse(saved);
  }

  private void aplicar(Monitor m, MonitorAdminRequest req) {
    m.setNombre(req.nombre);
    m.setDni(req.dni);
    m.setCorreoElectronico(req.correoElectronico);
    m.setContrasenya(passwordEncoder.encode(req.contrasena)); // guardamos hash
    m.setTelefono(req.telefono);
    m.setCiudad(req.ciudad);
    m.setDireccion(req.direccion);
    m.setCodigoPostal(req.codigoPostal);
  }

  private static MonitorAdminResponse toResponse(Monitor m) {
    return new MonitorAdminResponse(
        m.getId(),
        m.getNombre(),
        m.getDni(),
        m.getCorreoElectronico(),
        m.getTelefono(),
        m.getCiudad(),
        m.getDireccion(),
        m.getCodigoPostal()
    );
  }
}
