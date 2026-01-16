package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.SocioSession;
import com.fitgym.backend.api.dto.PreferenciasResponse;
import com.fitgym.backend.api.dto.PreferenciasUpdateRequest;
import com.fitgym.backend.api.error.GlobalExceptionHandler;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.domain.SocioEstado;
import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.service.InvalidCredentialsException;
import com.fitgym.backend.service.SocioInactivoException;
import com.fitgym.backend.service.SocioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.math.BigDecimal;

/**
 * Tests de controller para el flujo de autenticacion de socios.
 *
 * Cubre login, recuperacion de sesion (me) y logout con MockMvc.
 */
class SocioControllerAuthTest {

  private static final String SESSION_SOCIO_KEY = "socioLogin";

  private MockMvc mockMvc;

  private SocioService socioService;

  @BeforeEach
  void setUp() {
    socioService = Mockito.mock(SocioService.class);
    SocioController controller = new SocioController(socioService);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void login_ok_crea_sesion_y_devuelve_datos() throws Exception {
    Socio socio = buildSocio("test@fitgym.com", SocioEstado.ACTIVO);
    when(socioService.autenticar("test@fitgym.com", "Password123")).thenReturn(socio);

    String payload = "{\"correoElectronico\":\"test@fitgym.com\",\"contrasena\":\"Password123\"}";

    var result = mockMvc.perform(post("/api/socios/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.correoElectronico").value("test@fitgym.com"))
        .andReturn();

    MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
    assertThat(session).isNotNull();
    assertThat(session.getAttribute(SESSION_SOCIO_KEY)).isNotNull();
  }

  @Test
  void login_credenciales_invalidas_devuelve_401() throws Exception {
    when(socioService.autenticar("test@fitgym.com", "BadPass1"))
        .thenThrow(new InvalidCredentialsException("Credenciales invalidas."));

    String payload = "{\"correoElectronico\":\"test@fitgym.com\",\"contrasena\":\"BadPass1\"}";

    mockMvc.perform(post("/api/socios/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Credenciales invalidas."));
  }

  @Test
  void login_socio_inactivo_devuelve_403() throws Exception {
    when(socioService.autenticar("test@fitgym.com", "Password123"))
        .thenThrow(new SocioInactivoException("Socio inactivo."));

    String payload = "{\"correoElectronico\":\"test@fitgym.com\",\"contrasena\":\"Password123\"}";

    mockMvc.perform(post("/api/socios/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Socio inactivo."));
  }

  @Test
  void login_request_invalido_devuelve_400() throws Exception {
    String payload = "{\"correoElectronico\":\"badmail\",\"contrasena\":\"short\"}";

    mockMvc.perform(post("/api/socios/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void me_sin_sesion_devuelve_401() throws Exception {
    mockMvc.perform(get("/api/socios/me"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void me_con_sesion_invalida_devuelve_401() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SESSION_SOCIO_KEY, "valor-invalido");

    mockMvc.perform(get("/api/socios/me").session(session))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void me_con_sesion_devuelve_datos() throws Exception {
    SocioSession sessionData = new SocioSession(
        1L, "Test", "test@fitgym.com", "ACTIVO", 10L, "Basico", BigDecimal.ZERO, "123456789", "C/ Mayor 1", "Madrid", "28001");

    mockMvc.perform(get("/api/socios/me")
            .sessionAttr(SESSION_SOCIO_KEY, sessionData))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.correoElectronico").value("test@fitgym.com"))
        .andExpect(jsonPath("$.estado").value("ACTIVO"));
  }

  @Test
  void logout_devuelve_204() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SESSION_SOCIO_KEY, new SocioSession(
        1L, "Test", "test@fitgym.com", "ACTIVO", 10L, "Basico", BigDecimal.ZERO, "123456789", "C/ Mayor 1", "Madrid", "28001"));

    mockMvc.perform(post("/api/socios/logout").session(session))
        .andExpect(status().isNoContent());

    assertThat(session.isInvalid()).isTrue();
  }

  @Test
  void login_crea_sesion_y_me_la_recupera() throws Exception {
    Socio socio = buildSocio("test@fitgym.com", SocioEstado.ACTIVO);
    when(socioService.autenticar("test@fitgym.com", "Password123")).thenReturn(socio);

    String payload = "{\"correoElectronico\":\"test@fitgym.com\",\"contrasena\":\"Password123\"}";

    var loginResult = mockMvc.perform(post("/api/socios/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andReturn();

    MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
    assertThat(session).isNotNull();

    mockMvc.perform(get("/api/socios/me").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.correoElectronico").value("test@fitgym.com"));
  }

  @Test
  void logout_invalida_sesion_y_me_devuelve_401() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(SESSION_SOCIO_KEY, new SocioSession(
        1L, "Test", "test@fitgym.com", "ACTIVO", 10L, "Basico", BigDecimal.ZERO, "123456789", "C/ Mayor 1", "Madrid", "28001"));

    mockMvc.perform(post("/api/socios/logout").session(session))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/socios/me").session(session))
        .andExpect(status().isUnauthorized());
  }

    @Test
    void updateMe_sin_sesion_devuelve_401() throws Exception {
        String payload = """
      {"nombre":"Nuevo","telefono":"123","direccion":"Calle 1","ciudad":"Madrid","codigoPostal":"28001"}
      """;

        mockMvc.perform(put("/api/socios/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMe_con_sesion_invalida_devuelve_401() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_SOCIO_KEY, "valor-invalido");

        String payload = """
      {"nombre":"Nuevo","telefono":"123","direccion":"Calle 1","ciudad":"Madrid","codigoPostal":"28001"}
      """;

        mockMvc.perform(put("/api/socios/me")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMe_ok_actualiza_y_devuelve_datos() throws Exception {
        SocioSession sessionData = new SocioSession(
                1L, "Test", "test@fitgym.com", "ACTIVO", 10L, "Basico", BigDecimal.ZERO,
                null, null, null, null
        );

        Socio socioUpdated = buildSocio("test@fitgym.com", SocioEstado.ACTIVO);
        socioUpdated.setNombre("Juan Perez Diaz");
        socioUpdated.setTelefono("123456789");
        socioUpdated.setDireccion("Av. Sur 1");
        socioUpdated.setCiudad("Madrid");
        socioUpdated.setCodigoPostal("28001");

        when(socioService.actualizarDatosPersonales(
                1L,
                "Juan Perez Diaz",
                "123456789",
                "Av. Sur 1",
                "Madrid",
                "28001"
        )).thenReturn(socioUpdated);

        String payload = """
      {"nombre":"Juan Perez Diaz","telefono":"123456789","direccion":"Av. Sur 1","ciudad":"Madrid","codigoPostal":"28001"}
      """;

        mockMvc.perform(put("/api/socios/me")
                        .sessionAttr(SESSION_SOCIO_KEY, sessionData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Perez Diaz"))
                .andExpect(jsonPath("$.telefono").value("123456789"))
                .andExpect(jsonPath("$.direccion").value("Av. Sur 1"))
                .andExpect(jsonPath("$.ciudad").value("Madrid"))
                .andExpect(jsonPath("$.codigoPostal").value("28001"));
    }

    @Test
    void get_preferencias_con_sesion_devuelve_200_y_prefs() throws Exception {
        when(socioService.obtenerPreferencias(1L))
                .thenReturn(new PreferenciasResponse(true, false, true));

        SocioSession sessionData = new SocioSession(
                1L, "Test", "test@fitgym.com", "ACTIVO", 10L, "Basico", BigDecimal.ZERO,
                "123456789", "C/ Mayor 1", "Madrid", "28001"
        );

        mockMvc.perform(get("/api/socios/me/preferencias")
                        .sessionAttr(SESSION_SOCIO_KEY, sessionData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificaciones").value(true))
                .andExpect(jsonPath("$.recordatorios").value(false))
                .andExpect(jsonPath("$.comunicaciones").value(true));
    }

    @Test
    void put_preferencias_con_sesion_actualiza_y_devuelve_prefs() throws Exception {
        when(socioService.actualizarPreferencias(Mockito.eq(1L), Mockito.any()))
                .thenReturn(new PreferenciasResponse(false, true, false));

        SocioSession sessionData = new SocioSession(
                1L, "Test", "test@fitgym.com", "ACTIVO", 10L, "Basico", BigDecimal.ZERO,
                "123456789", "C/ Mayor 1", "Madrid", "28001"
        );

        String payload = """
    {"notificaciones":false,"recordatorios":true,"comunicaciones":false}
  """;

        mockMvc.perform(put("/api/socios/me/preferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .sessionAttr(SESSION_SOCIO_KEY, sessionData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificaciones").value(false))
                .andExpect(jsonPath("$.recordatorios").value(true))
                .andExpect(jsonPath("$.comunicaciones").value(false));
    }

    @Test
    void get_preferencias_sin_sesion_devuelve_401() throws Exception {
        mockMvc.perform(get("/api/socios/me/preferencias"))
                .andExpect(status().isUnauthorized());
    }

  private Socio buildSocio(String email, SocioEstado estado) {
    Tarifa tarifa = Mockito.mock(Tarifa.class);
    when(tarifa.getId()).thenReturn(10L);
    when(tarifa.getNombre()).thenReturn("Basico");

    Socio socio = new Socio();
    socio.setCorreoElectronico(email);
    socio.setEstado(estado);
    socio.setTarifa(tarifa);
    return socio;
  }
}
