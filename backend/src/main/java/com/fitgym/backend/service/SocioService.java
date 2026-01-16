package com.fitgym.backend.service;

import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.domain.SocioEstado;
import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.repo.SocioRepository;
import com.fitgym.backend.repo.TarifaRepository;
import com.fitgym.backend.api.dto.MembresiaResponse;
import com.fitgym.backend.api.dto.PreferenciasResponse;
import com.fitgym.backend.api.dto.PreferenciasUpdateRequest;
import java.time.OffsetDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import com.fitgym.backend.repo.PagoRegistroRepository;
import com.fitgym.backend.domain.PagoRegistro;
import com.fitgym.backend.domain.PagoRegistroEstado;
import java.time.ZoneOffset;

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
  private final PagoRegistroRepository pagoRegistroRepo;

  public SocioService(
      SocioRepository socioRepo,
      TarifaRepository tarifaRepo,
      PasswordEncoder passwordEncoder,
      PagoRegistroService pagoRegistroService,
      PagoRegistroRepository pagoRegistroRepo
  ) {
    this.socioRepo = socioRepo;
    this.tarifaRepo = tarifaRepo;
    this.passwordEncoder = passwordEncoder;
    this.pagoRegistroService = pagoRegistroService;
      this.pagoRegistroRepo = pagoRegistroRepo;
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

    socio.setTokenRegistro(paymentToken);

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

  // editar datos de perfil
  @Transactional
  public Socio actualizarDatosPersonales(
          Long socioId,
          String nombre,
          String telefono,
          String direccion,
          String ciudad,
          String codigoPostal
  ) {
      Socio socio = socioRepo.findById(socioId)
              .orElseThrow(() -> new EntityNotFoundException("Socio no encontrado."));

      socio.setNombre(nombre);
      socio.setTelefono(telefono);
      socio.setDireccion(direccion);
      socio.setCiudad(ciudad);
      socio.setCodigoPostal(codigoPostal);

      return socioRepo.save(socio);
  }

    @Transactional(readOnly = true)
    public MembresiaResponse obtenerMembresia(Long socioId) {
        Socio socio = socioRepo.findById(socioId)
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado."));

        // Ensure tarifa loaded
        socio.getTarifa().getId();
        socio.getTarifa().getNombre();
        socio.getTarifa().getCuota();

        String token = socio.getTokenRegistro();
        if (token == null || token.isBlank()) {
            return new MembresiaResponse(
                    socio.getTarifa().getId(),
                    socio.getTarifa().getNombre(),
                    socio.getTarifa().getCuota(),
                    "SIN_DATOS",
                    null,
                    null,
                    null
            );
        }

        PagoRegistro pr = pagoRegistroRepo.findByToken(token).orElse(null);

        OffsetDateTime fechaInicio = null;
        OffsetDateTime ultimoPago = null;
        OffsetDateTime proximaRenovacion = null;

        if (pr != null && pr.getCompletedAt() != null) {
            fechaInicio = pr.getCompletedAt().atOffset(ZoneOffset.UTC);
            ultimoPago = fechaInicio;
            proximaRenovacion = fechaInicio.plusMonths(1); // mensual
        }

        String estadoPago;
        if (socio.getEstado() != SocioEstado.ACTIVO) {
            estadoPago = "PENDIENTE";
        } else if (pr == null) {
            estadoPago = "SIN_DATOS";
        } else if (pr.getEstado() == PagoRegistroEstado.FAILED) {
            estadoPago = "IMPAGO";
        } else if (pr.getEstado() == PagoRegistroEstado.PENDING) {
            estadoPago = "PENDIENTE";
        } else if (pr.getEstado() == PagoRegistroEstado.COMPLETED) {
            // si quieres marcar IMPAGO cuando ya pasó la renovación y no hay pagos mensuales registrados:
            if (proximaRenovacion != null && OffsetDateTime.now(ZoneOffset.UTC).isAfter(proximaRenovacion)) {
                estadoPago = "IMPAGO";
            } else {
                estadoPago = "AL_DIA";
            }
        } else {
            estadoPago = "SIN_DATOS";
        }

        return new MembresiaResponse(
                socio.getTarifa().getId(),
                socio.getTarifa().getNombre(),
                socio.getTarifa().getCuota(),
                estadoPago,
                fechaInicio,
                proximaRenovacion,
                ultimoPago
        );
    }

    @Transactional
    public PreferenciasResponse actualizarPreferencias(
            Long socioId,
            PreferenciasUpdateRequest req
    ) {
        Socio socio = socioRepo.findById(socioId)
                .orElseThrow(() -> new EntityNotFoundException("Socio no encontrado"));

        socio.setPrefNotificaciones(req.notificaciones());
        socio.setPrefRecordatorios(req.recordatorios());
        socio.setPrefComunicaciones(req.comunicaciones());

        socioRepo.save(socio);

        return new PreferenciasResponse(
                socio.isPrefNotificaciones(),
                socio.isPrefRecordatorios(),
                socio.isPrefComunicaciones()
        );
    }

    @Transactional(readOnly = true)
    public PreferenciasResponse obtenerPreferencias(Long socioId) {
        Socio socio = socioRepo.findById(socioId)
                .orElseThrow(() -> new EntityNotFoundException("Socio no encontrado"));

        return new PreferenciasResponse(
                socio.isPrefNotificaciones(),
                socio.isPrefRecordatorios(),
                socio.isPrefComunicaciones()
        );
    }
}
