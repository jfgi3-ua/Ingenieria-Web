package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.PagoInitRequest;
import com.fitgym.backend.api.dto.PagoInitResponse;
import com.fitgym.backend.api.dto.PagoVerifyResponse;
import com.fitgym.backend.domain.PagoRegistro;
import com.fitgym.backend.service.PagoInitResult;
import com.fitgym.backend.service.PagoRegistroService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints propios para iniciar y verificar pagos de registro via TPVV.
 */
@RestController
@RequestMapping("/api/pagos")
public class PagoRegistroController {

  private final PagoRegistroService pagoRegistroService;

  public PagoRegistroController(PagoRegistroService pagoRegistroService) {
    this.pagoRegistroService = pagoRegistroService;
  }

  @PostMapping("/init")
  public ResponseEntity<PagoInitResponse> init(@Valid @RequestBody PagoInitRequest req) {
    PagoInitResult result = pagoRegistroService.iniciarPago(req.idTarifa);
    return ResponseEntity.ok(new PagoInitResponse(result.paymentUrl(), result.token()));
  }

  @PostMapping("/verify/{token}")
  public ResponseEntity<PagoVerifyResponse> verify(@PathVariable @NotBlank String token) {
    PagoRegistro pago = pagoRegistroService.verificarPago(token);
    return ResponseEntity.ok(new PagoVerifyResponse(
        pago.getEstado().name(),
        pago.getFailureReason()
    ));
  }
}
