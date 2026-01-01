package com.fitgym.backend.service;

import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.domain.SocioEstado;
import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.repo.SocioRepository;
import com.fitgym.backend.repo.TarifaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para la autenticacion de socios.
 *
 * Verifica que la logica de SocioService.autenticar:
 * - permita el acceso con credenciales correctas y estado ACTIVO
 * - rechace credenciales invalidas (401)
 * - rechace socios INACTIVOS (403)
 */
class SocioServiceAuthTest {

  private SocioRepository socioRepo;
  private TarifaRepository tarifaRepo;
  private PasswordEncoder passwordEncoder;
  private PagoRegistroService pagoRegistroService;
  private SocioService socioService;

  @BeforeEach
  void setUp() {
    socioRepo = Mockito.mock(SocioRepository.class);
    tarifaRepo = Mockito.mock(TarifaRepository.class);
    passwordEncoder = new BCryptPasswordEncoder();
    pagoRegistroService = Mockito.mock(PagoRegistroService.class);
    socioService = new SocioService(socioRepo, tarifaRepo, passwordEncoder, pagoRegistroService);
  }

  @Test
  void autenticar_con_credenciales_validas_retorna_socio() {
    Socio socio = buildSocio("test@fitgym.com", "Password123", SocioEstado.ACTIVO);

    when(socioRepo.findByCorreoElectronicoIgnoreCase("test@fitgym.com")).thenReturn(Optional.of(socio));

    Socio result = socioService.autenticar("test@fitgym.com", "Password123");
    assertEquals(socio, result);
  }

  @Test
  void autenticar_con_contrasena_invalida_lanza_invalid_credentials() {
    Socio socio = buildSocio("test@fitgym.com", "Password123", SocioEstado.ACTIVO);

    when(socioRepo.findByCorreoElectronicoIgnoreCase("test@fitgym.com")).thenReturn(Optional.of(socio));

    assertThrows(InvalidCredentialsException.class,
        () -> socioService.autenticar("test@fitgym.com", "WrongPassword"));
  }

  @Test
  void autenticar_con_socio_inactivo_lanza_socio_inactivo() {
    Socio socio = buildSocio("test@fitgym.com", "Password123", SocioEstado.INACTIVO);

    when(socioRepo.findByCorreoElectronicoIgnoreCase("test@fitgym.com")).thenReturn(Optional.of(socio));

    assertThrows(SocioInactivoException.class,
        () -> socioService.autenticar("test@fitgym.com", "Password123"));
  }

  private Socio buildSocio(String email, String rawPassword, SocioEstado estado) {
    Tarifa tarifa = Mockito.mock(Tarifa.class);
    when(tarifa.getId()).thenReturn(1L);
    when(tarifa.getNombre()).thenReturn("Basico");

    Socio socio = new Socio();
    socio.setCorreoElectronico(email);
    socio.setContrasenaHash(passwordEncoder.encode(rawPassword));
    socio.setEstado(estado);
    socio.setTarifa(tarifa);
    return socio;
  }
}
