package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.PagoInitRequest;
import com.fitgym.backend.api.error.GlobalExceptionHandler;
import com.fitgym.backend.domain.PagoRegistro;
import com.fitgym.backend.domain.PagoRegistroEstado;
import com.fitgym.backend.service.PagoInitResult;
import com.fitgym.backend.service.PagoRegistroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PagoRegistroControllerTest {

  private MockMvc mockMvc;
  private PagoRegistroService pagoRegistroService;

  @BeforeEach
  void setUp() {
    pagoRegistroService = Mockito.mock(PagoRegistroService.class);
    PagoRegistroController controller = new PagoRegistroController(pagoRegistroService);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void init_devuelve_paymentUrl_y_token() throws Exception {
    when(pagoRegistroService.iniciarPago(1L))
        .thenReturn(new PagoInitResult("https://tpv.example/checkout?token=abc", "abc"));

    String payload = "{\"idTarifa\":1}";

    mockMvc.perform(post("/api/pagos/init")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.paymentUrl").value("https://tpv.example/checkout?token=abc"))
        .andExpect(jsonPath("$.token").value("abc"));
  }

  @Test
  void verify_devuelve_estado_y_failureReason() throws Exception {
    PagoRegistro pago = new PagoRegistro();
    pago.setEstado(PagoRegistroEstado.FAILED);
    pago.setFailureReason("Saldo insuficiente");

    when(pagoRegistroService.verificarPago("tok")).thenReturn(pago);

    mockMvc.perform(post("/api/pagos/verify/tok"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("FAILED"))
        .andExpect(jsonPath("$.failureReason").value("Saldo insuficiente"));
  }
}
