package com.fitgym.backend.service;

import com.fitgym.backend.domain.PagoMonedero;
import com.fitgym.backend.domain.PagoRegistroEstado;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.repo.PagoMonederoRepository;
import com.fitgym.backend.repo.SocioRepository;
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

@Service
public class PagoMonederoService {

    private final PagoMonederoRepository pagoRepo;
    private final SocioRepository socioRepo;
    private final TpvvClient tpvvClient;
    private final TpvvProperties props;

    public PagoMonederoService(
            PagoMonederoRepository pagoRepo,
            SocioRepository socioRepo,
            TpvvClient tpvvClient,
            TpvvProperties props
    ) {
        this.pagoRepo = pagoRepo;
        this.socioRepo = socioRepo;
        this.tpvvClient = tpvvClient;
        this.props = props;
    }

    @Transactional
    public PagoInitResult iniciarRecarga(Long socioId, BigDecimal importe) {
        if (importe == null || importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Importe inválido.");
        }

        // ensure socio exists
        socioRepo.findById(socioId).orElseThrow(() -> new IllegalArgumentException("Socio no encontrado."));

        String externalReference = "WAL-" + UUID.randomUUID();
        if (props.callbackUrlMonedero() == null || props.callbackUrlMonedero().isBlank()) {
            throw new TpvvCommunicationException("TPVV callbackUrl no configurada.");
        }

        TpvvPaymentInitRequest payload = new TpvvPaymentInitRequest(
                importe,
                props.callbackUrlMonedero(),
                externalReference
        );

        TpvvPaymentInitResponse response = tpvvClient.initPayment(payload);
        if (response == null || response.token() == null || response.paymentUrl() == null) {
            throw new TpvvCommunicationException("TPVV devolvió una respuesta incompleta.");
        }

        String paymentUrl = normalizePaymentUrl(response.paymentUrl());

        PagoMonedero pago = new PagoMonedero();
        pago.setToken(response.token());
        pago.setEstado(PagoRegistroEstado.PENDING);
        pago.setImporte(importe);
        pago.setIdSocio(socioId);
        pago.setExternalReference(externalReference);
        pago.setCallbackUrl(props.callbackUrlMonedero());
        pago.setPaymentUrl(paymentUrl);

        pagoRepo.save(pago);

        return new PagoInitResult(paymentUrl, response.token());
    }

    @Transactional
    public PagoMonedero verificarRecarga(String token) {
        PagoMonedero pago = pagoRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("No existe una recarga con ese token."));

        // If already completed/failed, do not double-process
        if (pago.getEstado() == PagoRegistroEstado.COMPLETED || pago.getEstado() == PagoRegistroEstado.FAILED) {
            return pago;
        }

        TpvvPaymentVerifyResponse response = tpvvClient.verifyPayment(token);
        if (response == null || response.status() == null) {
            throw new TpvvCommunicationException("TPVV devolvió estado inválido.");
        }

        PagoRegistroEstado nuevoEstado = mapEstado(response.status());
        pago.setEstado(nuevoEstado);
        pago.setProviderStatus(response.status());
        pago.setFailureReason(response.failureReason());

        if (nuevoEstado == PagoRegistroEstado.COMPLETED) {
            pago.setCompletedAt(Instant.now());
            pago.setFailedAt(null);

            Socio socio = socioRepo.findById(pago.getIdSocio())
                    .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado."));
            socio.setSaldoMonedero(socio.getSaldoMonedero().add(pago.getImporte()));
            socioRepo.save(socio);

        } else if (nuevoEstado == PagoRegistroEstado.FAILED) {
            pago.setFailedAt(Instant.now());
            pago.setCompletedAt(null);
        }

        return pagoRepo.save(pago);
    }

    private PagoRegistroEstado mapEstado(String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if ("COMPLETED".equals(normalized)) return PagoRegistroEstado.COMPLETED;
        if ("FAILED".equals(normalized)) return PagoRegistroEstado.FAILED;
        return PagoRegistroEstado.PENDING;
    }

    private String normalizePaymentUrl(String paymentUrl) {
        String trimmed = paymentUrl.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed;

        String base = props.baseUrl();
        if (base == null || base.isBlank()) throw new TpvvCommunicationException("TPVV baseUrl no configurada.");

        String baseNormalized = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String path = trimmed.startsWith("/") ? trimmed : "/" + trimmed;
        return baseNormalized + path;
    }
}