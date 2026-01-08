package com.fitgym.backend.service;

import com.fitgym.backend.domain.PagoRegistro;
import com.fitgym.backend.domain.PagoRegistroEstado;
import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.repo.PagoRegistroRepository;
import com.fitgym.backend.repo.TarifaRepository;
import com.fitgym.backend.service.tpvv.TpvvClient;
import com.fitgym.backend.service.tpvv.TpvvProperties;
import com.fitgym.backend.service.tpvv.dto.TpvvPaymentInitRequest;
import com.fitgym.backend.service.tpvv.dto.TpvvPaymentInitResponse;
import com.fitgym.backend.service.tpvv.dto.TpvvPaymentVerifyResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquesta el flujo de pago de registro con TPVV.
 *
 * - inicia transacciones PENDING
 * - verifica el estado con TPVV
 * - valida que el pago este COMPLETED antes de registrar un socio
 */
@Service
public class PagoRegistroService {

  private final PagoRegistroRepository pagoRepo;
  private final TarifaRepository tarifaRepo;
  private final TpvvClient tpvvClient;
  private final TpvvProperties props;

  public PagoRegistroService(
      PagoRegistroRepository pagoRepo,
      TarifaRepository tarifaRepo,
      TpvvClient tpvvClient,
      TpvvProperties props
  ) {
    this.pagoRepo = pagoRepo;
    this.tarifaRepo = tarifaRepo;
    this.tpvvClient = tpvvClient;
    this.props = props;
  }

  @Transactional
  public PagoInitResult iniciarPago(Long idTarifa) {
    Tarifa tarifa = tarifaRepo.findById(idTarifa)
        .orElseThrow(() -> new TarifaNotFoundException("La tarifa indicada no existe."));

    BigDecimal importe = tarifa.getCuota();
    String externalReference = "REG-" + UUID.randomUUID();
    if (props.callbackUrl() == null || props.callbackUrl().isBlank()) {
      throw new TpvvCommunicationException("TPVV callbackUrl no configurada.");
    }

    TpvvPaymentInitRequest payload = new TpvvPaymentInitRequest(
        importe,
        props.callbackUrl(),
        externalReference
    );

    TpvvPaymentInitResponse response = tpvvClient.initPayment(payload);
    if (response == null || response.token() == null || response.paymentUrl() == null) {
      throw new TpvvCommunicationException("TPVV devolvio una respuesta incompleta al iniciar el pago.");
    }

    String paymentUrl = normalizePaymentUrl(response.paymentUrl());

    PagoRegistro pago = new PagoRegistro();
    pago.setToken(response.token());
    pago.setEstado(PagoRegistroEstado.PENDING);
    pago.setImporte(importe);
    pago.setExternalReference(externalReference);
    pago.setCallbackUrl(props.callbackUrl());
    pago.setPaymentUrl(paymentUrl);
    pagoRepo.save(pago);

    return new PagoInitResult(paymentUrl, response.token());
  }

  @Transactional
  public PagoRegistro verificarPago(String token) {
    PagoRegistro pago = pagoRepo.findByToken(token)
        .orElseThrow(() -> new PagoRegistroNotFoundException("No existe un pago con ese token."));

    TpvvPaymentVerifyResponse response = tpvvClient.verifyPayment(token);
    if (response == null || response.status() == null) {
      throw new TpvvCommunicationException("TPVV devolvio un estado invalido al verificar el pago.");
    }

    PagoRegistroEstado nuevoEstado = mapEstado(response.status());
    pago.setEstado(nuevoEstado);
    pago.setProviderStatus(response.status());
    pago.setFailureReason(response.failureReason());

    if (nuevoEstado == PagoRegistroEstado.COMPLETED) {
      pago.setCompletedAt(Instant.now());
      pago.setFailedAt(null);
    } else if (nuevoEstado == PagoRegistroEstado.FAILED) {
      pago.setFailedAt(Instant.now());
      pago.setCompletedAt(null);
    }

    return pagoRepo.save(pago);
  }

  @Transactional
  public void exigirPagoCompletado(String token, BigDecimal importeEsperado) {
    PagoRegistro pago = verificarPago(token);

    if (pago.getEstado() != PagoRegistroEstado.COMPLETED) {
      throw new PagoRegistroNoCompletadoException("El pago no esta completado.");
    }

    if (pago.getImporte() != null && pago.getImporte().compareTo(importeEsperado) != 0) {
      throw new PagoRegistroNoCompletadoException("El importe del pago no coincide con la tarifa.");
    }
  }

  private PagoRegistroEstado mapEstado(String status) {
    String normalized = status.trim().toUpperCase(Locale.ROOT);
    if ("COMPLETED".equals(normalized)) {
      return PagoRegistroEstado.COMPLETED;
    }
    if ("FAILED".equals(normalized)) {
      return PagoRegistroEstado.FAILED;
    }
    return PagoRegistroEstado.PENDING;
  }

  private String normalizePaymentUrl(String paymentUrl) {
    String trimmed = paymentUrl.trim();
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
      return trimmed;
    }

    String base = props.baseUrl();
    if (base == null || base.isBlank()) {
      throw new TpvvCommunicationException("TPVV baseUrl no configurada.");
    }

    String baseNormalized = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    String path = trimmed.startsWith("/") ? trimmed : "/" + trimmed;
    return baseNormalized + path;
  }
}
