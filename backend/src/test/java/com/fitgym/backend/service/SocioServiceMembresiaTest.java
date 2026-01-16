package com.fitgym.backend.service;

import com.fitgym.backend.api.dto.MembresiaResponse;
import com.fitgym.backend.domain.PagoRegistro;
import com.fitgym.backend.domain.PagoRegistroEstado;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.domain.SocioEstado;
import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.repo.PagoRegistroRepository;
import com.fitgym.backend.repo.SocioRepository;
import com.fitgym.backend.repo.TarifaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SocioServiceMembresiaTest {

    private SocioRepository socioRepo;
    private TarifaRepository tarifaRepo;
    private PasswordEncoder passwordEncoder;
    private PagoRegistroService pagoRegistroService;
    private PagoRegistroRepository pagoRegistroRepo;

    private SocioService socioService;

    @BeforeEach
    void setUp() {
        socioRepo = Mockito.mock(SocioRepository.class);
        tarifaRepo = Mockito.mock(TarifaRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        pagoRegistroService = Mockito.mock(PagoRegistroService.class);
        pagoRegistroRepo = Mockito.mock(PagoRegistroRepository.class);

        socioService = new SocioService(socioRepo, tarifaRepo, passwordEncoder, pagoRegistroService, pagoRegistroRepo);
    }

    @Test
    void obtenerMembresia_con_pago_registro_completado_devuelve_al_dia() {
        Socio socio = buildSocioActivoConToken(1L, "token-123");

        PagoRegistro pr = new PagoRegistro();
        pr.setEstado(PagoRegistroEstado.COMPLETED);
        pr.setCompletedAt(Instant.parse("2026-01-16T00:00:00Z"));

        when(socioRepo.findById(1L)).thenReturn(Optional.of(socio));
        when(pagoRegistroRepo.findByToken("token-123")).thenReturn(Optional.of(pr));

        MembresiaResponse res = socioService.obtenerMembresia(1L);

        assertEquals("AL_DIA", res.estadoPago());
        assertNotNull(res.fechaInicio());
        assertNotNull(res.proximaRenovacion());
    }

    @Test
    void obtenerMembresia_sin_token_devuelve_sin_datos() {
        Socio socio = buildSocioActivoSinToken(1L);

        when(socioRepo.findById(1L)).thenReturn(Optional.of(socio));

        MembresiaResponse res = socioService.obtenerMembresia(1L);

        assertEquals("SIN_DATOS", res.estadoPago());
        assertNull(res.fechaInicio());
        assertNull(res.proximaRenovacion());
        assertNull(res.ultimoPago());
    }

    @Test
    void obtenerMembresia_con_renovacion_vencida_devuelve_impago() {
        Socio socio = buildSocioActivoConToken(1L, "token-123");

        PagoRegistro pr = new PagoRegistro();
        pr.setEstado(PagoRegistroEstado.COMPLETED);
        pr.setCompletedAt(Instant.now().minus(40, ChronoUnit.DAYS));

        when(socioRepo.findById(1L)).thenReturn(Optional.of(socio));
        when(pagoRegistroRepo.findByToken("token-123")).thenReturn(Optional.of(pr));

        MembresiaResponse res = socioService.obtenerMembresia(1L);

        assertEquals("IMPAGO", res.estadoPago());
    }

    private Socio buildSocioActivoConToken(Long id, String token) {
        Socio socio = buildBaseSocio(id);
        socio.setEstado(SocioEstado.ACTIVO);
        socio.setTokenRegistro(token);
        return socio;
    }

    private Socio buildSocioActivoSinToken(Long id) {
        Socio socio = buildBaseSocio(id);
        socio.setEstado(SocioEstado.ACTIVO);
        socio.setTokenRegistro(null); // o "" si quieres probar blank
        return socio;
    }

    private Socio buildBaseSocio(Long id) {
        Tarifa tarifa = Mockito.mock(Tarifa.class);
        when(tarifa.getId()).thenReturn(1L);
        when(tarifa.getNombre()).thenReturn("Basico");
        when(tarifa.getCuota()).thenReturn(new BigDecimal("29.99"));

        Socio socio = new Socio();
        socio.setNombre("Usuario");
        socio.setCorreoElectronico("usuario@ejemplo.com");
        socio.setContrasenaHash(passwordEncoder.encode("Password123"));
        socio.setTarifa(tarifa);

        return socio;
    }
}