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

  // Metodo para disminuir las clases gratis de la tarifa
  @Transactional
  public boolean disminuirClasesGratisDeTarifa(Long id){
    Socio socio = socioRepo.findById(id).orElse(null);

    if(socio == null){
      return false;
    }

    Integer total = socio.getClasesGratis();
    socio.setClasesGratis(total-1);

    return true;
  }

  /////////////

    @Transactional(readOnly = true)
  public java.util.List<Socio> listarTodos() {
    return socioRepo.findAll(org.springframework.data.domain.Sort.by("id").ascending());
  }

  @Transactional(readOnly = true)
  public Socio obtenerPorId(Long id) {
    Socio socio = socioRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado: " + id));
    // Forzar carga tarifa dentro de transacción
    socio.getTarifa().getId();
    socio.getTarifa().getNombre();
    return socio;
  }

  @Transactional
  public Socio actualizarComoAdmin(Long id, com.fitgym.backend.api.dto.SocioAdminUpdateRequest req) {
    Socio socio = socioRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado: " + id));

    // Si cambia el email, comprobar duplicado
    String nuevoEmail = req.correoElectronico != null ? req.correoElectronico.trim() : null;
    if (nuevoEmail != null && !nuevoEmail.equalsIgnoreCase(socio.getCorreoElectronico())) {
      if (socioRepo.existsByCorreoElectronicoIgnoreCase(nuevoEmail)) {
        throw new DuplicateEmailException("Ya existe un socio con ese correo electronico.");
      }
      socio.setCorreoElectronico(nuevoEmail);
    }

    socio.setNombre(req.nombre);
    socio.setTelefono(req.telefono);
    socio.setDireccion(req.direccion);
    socio.setCiudad(req.ciudad);
    socio.setCodigoPostal(req.codigoPostal);

    if (req.pagoDomiciliado != null) {
      socio.setPagoDomiciliado(req.pagoDomiciliado);
    }

    Tarifa tarifa = tarifaRepo.findById(req.idTarifa)
        .orElseThrow(() -> new TarifaNotFoundException("La tarifa indicada no existe."));
    socio.setTarifa(tarifa);
    socio.setClasesGratis(tarifa.getClasesGratisMes());

    // Forzar carga de tarifa para la respuesta
    socio.getTarifa().getId();
    socio.getTarifa().getNombre();

    return socio;
  }

  @Transactional
  public Socio cambiarEstado(Long id, SocioEstado nuevoEstado) {
    Socio socio = socioRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado: " + id));

    socio.setEstado(nuevoEstado);

    // Forzar carga tarifa
    socio.getTarifa().getId();
    socio.getTarifa().getNombre();

    return socio;
  }

}
