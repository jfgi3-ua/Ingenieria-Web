package com.fitgym.backend.service;

import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.domain.SocioEstado;
import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.repo.SocioRepository;
import com.fitgym.backend.repo.TarifaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de gestion de socios del gimnasio.
 *
 * Proporciona operaciones para el registro y administracion de socios,
 * incluyendo validaciones de datos, gestion de tarifas y codificacion segura de contrasenas.
 */
@Service
public class SocioService {

  private final SocioRepository socioRepo;
  private final TarifaRepository tarifaRepo;
  private final PasswordEncoder passwordEncoder;
  private final PagoRegistroService pagoRegistroService;

  public SocioService(
      SocioRepository socioRepo,
      TarifaRepository tarifaRepo,
      PasswordEncoder passwordEncoder,
      PagoRegistroService pagoRegistroService
  ) {
    this.socioRepo = socioRepo;
    this.tarifaRepo = tarifaRepo;
    this.passwordEncoder = passwordEncoder;
    this.pagoRegistroService = pagoRegistroService;
  }

  /**
   * Autentica a un socio por correo y contrasena.
   */
  @Transactional(readOnly = true)
  public Socio autenticar(String correo, String passwordPlano) {
    Socio socio = socioRepo.findByCorreoElectronicoIgnoreCase(correo)
        .orElseThrow(() -> new InvalidCredentialsException("Credenciales invalidas."));

    if (!passwordEncoder.matches(passwordPlano, socio.getContrasenaHash())) {
      throw new InvalidCredentialsException("Credenciales invalidas.");
    }

    if (socio.getEstado() != SocioEstado.ACTIVO) {
      throw new SocioInactivoException("Socio inactivo. Pendiente de aceptacion.");
    }

    // Fuerza la carga de la tarifa dentro de la transaccion.
    socio.getTarifa().getId();
    socio.getTarifa().getNombre();

    return socio;
  }

  /**
   * Comprueba si ya existe un socio con el correo indicado.
   */
  @Transactional(readOnly = true)
  public boolean emailExiste(String correo) {
    return socioRepo.existsByCorreoElectronicoIgnoreCase(correo);
  }

  /**
   * Registra un nuevo socio en el sistema.
   *
   * El backend valida que el pago TPVV este COMPLETED antes de persistir el socio.
   */
  @Transactional
  public Socio registrar(
      String nombre,
      String correo,
      String passwordPlano,
      String telefono,
      Long idTarifa,
      String direccion,
      String ciudad,
      String codigoPostal,
      String paymentToken
  ) {
    if (socioRepo.existsByCorreoElectronicoIgnoreCase(correo)) {
      throw new DuplicateEmailException("Ya existe un socio con ese correo electronico.");
    }

    Tarifa tarifa = tarifaRepo.findById(idTarifa)
        .orElseThrow(() -> new TarifaNotFoundException("La tarifa indicada no existe."));

    // El backend es la fuente de verdad: valida en TPVV antes de registrar.
    pagoRegistroService.exigirPagoCompletado(paymentToken, tarifa.getCuota());

    Socio socio = new Socio();
    socio.setNombre(nombre);
    socio.setCorreoElectronico(correo);
    socio.setContrasenaHash(passwordEncoder.encode(passwordPlano));
    socio.setTelefono(telefono);

    socio.setTarifa(tarifa);
    socio.setClasesGratis(tarifa.getClasesGratisMes());

    // Solicitud pendiente de aceptacion.
    socio.setEstado(SocioEstado.INACTIVO);

    socio.setDireccion(direccion);
    socio.setCiudad(ciudad);
    socio.setCodigoPostal(codigoPostal);

    return socioRepo.save(socio);
  }
}
