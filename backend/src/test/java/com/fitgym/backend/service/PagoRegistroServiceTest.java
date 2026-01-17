package com.fitgym.backend.service;

import com.fitgym.backend.domain.PagoRegistro;
import com.fitgym.backend.domain.PagoRegistroEstado;
import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.repo.PagoRegistroRepository;
import com.fitgym.backend.repo.TarifaRepository;
import com.fitgym.backend.service.tpvv.TpvvClient;
import com.fitgym.backend.service.tpvv.TpvvProperties;
import com.fitgym.backend.service.tpvv.dto.TpvvPaymentInitResponse;
import com.fitgym.backend.service.tpvv.dto.TpvvPaymentVerifyResponse;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PagoRegistroServiceTest {

  private PagoRegistroRepository pagoRepo;
  private TarifaRepository tarifaRepo;
  private TpvvClient tpvvClient;
  private PagoRegistroService service;

  @BeforeEach
  void setUp() {
    pagoRepo = Mockito.mock(PagoRegistroRepository.class);
    tarifaRepo = Mockito.mock(TarifaRepository.class);
    tpvvClient = Mockito.mock(TpvvClient.class);

    TpvvProperties props = new TpvvProperties(
        "https://tpv.example",
        "sk_test",
        "http://localhost:5173/registro",
        "http://localhost:5173/perfil"
    );

    service = new PagoRegistroService(pagoRepo, tarifaRepo, tpvvClient, props);
  }

  @Test
  void iniciarPago_creaPagoPendiente_y_normalizaPaymentUrl() {
    Tarifa tarifa = new Tarifa();
    tarifa.setCuota(new BigDecimal("49.99"));
    when(tarifaRepo.findById(1L)).thenReturn(Optional.of(tarifa));
    when(tpvvClient.initPayment(any())).thenReturn(new TpvvPaymentInitResponse("/checkout?token=abc", "abc"));
    when(pagoRepo.save(any(PagoRegistro.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PagoInitResult result = service.iniciarPago(1L);

    assertEquals("https://tpv.example/checkout?token=abc", result.paymentUrl());
    assertEquals("abc", result.token());

    ArgumentCaptor<PagoRegistro> captor = ArgumentCaptor.forClass(PagoRegistro.class);
    Mockito.verify(pagoRepo).save(captor.capture());

    PagoRegistro saved = captor.getValue();
    assertEquals(PagoRegistroEstado.PENDING, saved.getEstado());
    assertEquals("abc", saved.getToken());
    assertEquals(new BigDecimal("49.99"), saved.getImporte());
    assertEquals("https://tpv.example/checkout?token=abc", saved.getPaymentUrl());
  }

  @Test
  void verificarPago_actualizaEstadoCompleted() {
    PagoRegistro pago = new PagoRegistro();
    pago.setToken("tok");
    pago.setImporte(new BigDecimal("10.00"));
    when(pagoRepo.findByToken("tok")).thenReturn(Optional.of(pago));
    when(tpvvClient.verifyPayment("tok")).thenReturn(new TpvvPaymentVerifyResponse("COMPLETED", null));
    when(pagoRepo.save(any(PagoRegistro.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PagoRegistro result = service.verificarPago("tok");

    assertEquals(PagoRegistroEstado.COMPLETED, result.getEstado());
    assertEquals("COMPLETED", result.getProviderStatus());
    assertNotNull(result.getCompletedAt());
    assertNull(result.getFailedAt());
  }

  @Test
  void verificarPago_actualizaEstadoFailed() {
    PagoRegistro pago = new PagoRegistro();
    pago.setToken("tok");
    pago.setImporte(new BigDecimal("10.00"));
    when(pagoRepo.findByToken("tok")).thenReturn(Optional.of(pago));
    when(tpvvClient.verifyPayment("tok")).thenReturn(new TpvvPaymentVerifyResponse("FAILED", "Saldo insuficiente"));
    when(pagoRepo.save(any(PagoRegistro.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PagoRegistro result = service.verificarPago("tok");

    assertEquals(PagoRegistroEstado.FAILED, result.getEstado());
    assertEquals("FAILED", result.getProviderStatus());
    assertEquals("Saldo insuficiente", result.getFailureReason());
    assertNotNull(result.getFailedAt());
    assertNull(result.getCompletedAt());
  }

  @Test
  void exigirPagoCompletado_lanza_si_estado_no_completed() {
    PagoRegistro pago = new PagoRegistro();
    pago.setToken("tok");
    pago.setImporte(new BigDecimal("10.00"));
    when(pagoRepo.findByToken("tok")).thenReturn(Optional.of(pago));
    when(tpvvClient.verifyPayment("tok")).thenReturn(new TpvvPaymentVerifyResponse("PENDING", null));
    when(pagoRepo.save(any(PagoRegistro.class))).thenAnswer(invocation -> invocation.getArgument(0));

    assertThrows(PagoRegistroNoCompletadoException.class,
        () -> service.exigirPagoCompletado("tok", new BigDecimal("10.00")));
  }

  @Test
  void exigirPagoCompletado_lanza_si_importe_no_coincide() {
    PagoRegistro pago = new PagoRegistro();
    pago.setToken("tok");
    pago.setImporte(new BigDecimal("10.00"));
    when(pagoRepo.findByToken("tok")).thenReturn(Optional.of(pago));
    when(tpvvClient.verifyPayment("tok")).thenReturn(new TpvvPaymentVerifyResponse("COMPLETED", null));
    when(pagoRepo.save(any(PagoRegistro.class))).thenAnswer(invocation -> invocation.getArgument(0));

    assertThrows(PagoRegistroNoCompletadoException.class,
        () -> service.exigirPagoCompletado("tok", new BigDecimal("20.00")));
  }
}
