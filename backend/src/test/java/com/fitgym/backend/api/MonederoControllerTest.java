package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.SocioSession;
import com.fitgym.backend.api.error.GlobalExceptionHandler;
import com.fitgym.backend.domain.PagoMonedero;
import com.fitgym.backend.domain.PagoRegistroEstado;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.domain.SocioEstado;
import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.service.PagoInitResult;
import com.fitgym.backend.service.PagoMonederoService;
import com.fitgym.backend.service.SocioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MonederoControllerTest {

    private static final String SESSION_SOCIO_KEY = "socioLogin";

    private MockMvc mockMvc;
    private PagoMonederoService pagoMonederoService;
    private SocioService socioService;

    @BeforeEach
    void setup() {
        pagoMonederoService = Mockito.mock(PagoMonederoService.class);
        socioService = Mockito.mock(SocioService.class);

        MonederoController controller = new MonederoController(pagoMonederoService, socioService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void recarga_sin_sesion_devuelve_401() throws Exception {
        mockMvc.perform(post("/api/monedero/recarga")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"importe\":10}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void recarga_con_sesion_ok_devuelve_paymentUrl_y_token() throws Exception {
        MockHttpSession session = sessionConSocio();

        when(pagoMonederoService.iniciarRecarga(Mockito.eq(1L), Mockito.any()))
                .thenReturn(new PagoInitResult("https://tpvv/pay", "tok123"));

        mockMvc.perform(post("/api/monedero/recarga")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"importe\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentUrl").value("https://tpvv/pay"))
                .andExpect(jsonPath("$.token").value("tok123"));
    }

    @Test
    void verify_sin_sesion_devuelve_401() throws Exception {
        mockMvc.perform(post("/api/monedero/verify/tok123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verify_con_sesion_devuelve_estado() throws Exception {
        MockHttpSession session = sessionConSocio();

        PagoMonedero pago = new PagoMonedero();
        pago.setToken("tok123");
        pago.setEstado(PagoRegistroEstado.COMPLETED);
        pago.setFailureReason(null);

        when(pagoMonederoService.verificarRecarga("tok123")).thenReturn(pago);

        Tarifa tarifa = Mockito.mock(Tarifa.class);
        when(tarifa.getId()).thenReturn(10L);
        when(tarifa.getNombre()).thenReturn("Basico");

        Socio socio = new Socio();
        socio.setNombre("Test");
        socio.setCorreoElectronico("test@fitgym.com");
        socio.setEstado(SocioEstado.ACTIVO);
        socio.setTarifa(tarifa);
        socio.setSaldoMonedero(BigDecimal.valueOf(50));

        when(socioService.obtenerPorId(1L)).thenReturn(socio);

        mockMvc.perform(post("/api/monedero/verify/tok123").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETED"))
                .andExpect(jsonPath("$.failureReason").value(org.hamcrest.Matchers.nullValue()));
    }

    private MockHttpSession sessionConSocio() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_SOCIO_KEY, new SocioSession(
                1L, "Test", "test@fitgym.com", "ACTIVO", 10L, "Basico",
                BigDecimal.ZERO, null, null, null, null
        ));
        return session;
    }

    private Socio buildSocioConTarifa() {
        Tarifa tarifa = Mockito.mock(Tarifa.class);
        when(tarifa.getId()).thenReturn(10L);
        when(tarifa.getNombre()).thenReturn("Basico");

        Socio socio = new Socio();
        socio.setNombre("Test");
        socio.setCorreoElectronico("test@fitgym.com");
        socio.setEstado(SocioEstado.ACTIVO);
        socio.setTarifa(tarifa);
        socio.setSaldoMonedero(BigDecimal.valueOf(50)); // cualquier valor válido
        return socio;
    }
}