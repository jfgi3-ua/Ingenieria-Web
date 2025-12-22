package com.fitgym.backend.service;

import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.domain.SocioEstado;
import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.repo.SocioRepository;
import com.fitgym.backend.repo.TarifaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 import java.util.List;

/**
 * Servicio de gestión de socios del gimnasio.
 *
 * Proporciona operaciones para el registro y administración de socios,
 * incluyendo validaciones de datos, gestión de tarifas y codificación segura de contraseñas.
 */
@Service
public class SocioService {

  private final SocioRepository socioRepo;
  private final TarifaRepository tarifaRepo;
  private final PasswordEncoder passwordEncoder;


  public SocioService(SocioRepository socioRepo, TarifaRepository tarifaRepo, PasswordEncoder passwordEncoder) {
    this.socioRepo = socioRepo;
    this.tarifaRepo = tarifaRepo;
    this.passwordEncoder = passwordEncoder;
  }

/**
 * Registra un nuevo socio en el sistema.
 *
 * Realiza el registro completo de un socio con validación de datos y asignación de tarifa.
 * La contraseña se codifica de forma segura antes de almacenarse. El socio se crea
 * inicialmente en estado INACTIVO, pendiente de aceptación.
 *
 * @param nombre          El nombre completo del socio. No puede ser nulo.
 * @param correo          El correo electrónico del socio. Debe ser único en el sistema
 *                        y la búsqueda es insensible a mayúsculas/minúsculas.
 * @param passwordPlano   La contraseña en texto plano proporcionada por el usuario.
 *                        Se codificará antes de almacenarse en la base de datos.
 * @param telefono        El número de teléfono de contacto del socio.
 * @param idTarifa        El identificador de la tarifa a asignar al socio.
 *                        Debe existir en el sistema.
 * @param direccion       La dirección física de residencia del socio.
 * @param ciudad          La ciudad de residencia del socio.
 * @param codigoPostal    El código postal de la dirección del socio.
 *
 * @return El objeto {@link Socio} creado y almacenado en la base de datos.
 *
 * @throws BusinessException Si ya existe un socio con el correo electrónico proporcionado.
 * @throws BusinessException Si la tarifa con el identificador especificado no existe.
 *
 * @see Socio
 * @see Tarifa
 * @see SocioEstado#INACTIVO
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
      String codigoPostal
  ) {
    if (socioRepo.existsByCorreoElectronicoIgnoreCase(correo)) {
      throw new BusinessException("Ya existe un socio con ese correo electrónico.");
    }

    Tarifa tarifa = tarifaRepo.findById(idTarifa)
        .orElseThrow(() -> new BusinessException("La tarifa indicada no existe."));

    Socio socio = new Socio();
    socio.setNombre(nombre);
    socio.setCorreoElectronico(correo);
    socio.setContrasenaHash(passwordEncoder.encode(passwordPlano));
    socio.setTelefono(telefono);

    socio.setTarifa(tarifa);

    // “Solicitud pendiente de aceptación”
    socio.setEstado(SocioEstado.INACTIVO);

    socio.setDireccion(direccion);
    socio.setCiudad(ciudad);
    socio.setCodigoPostal(codigoPostal);

    return socioRepo.save(socio);

  }
  public List<Socio> listarTodos() {
    return socioRepo.findAll();
  }
}
