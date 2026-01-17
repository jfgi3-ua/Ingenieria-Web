package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.MonederoRecargaRequest;
import com.fitgym.backend.api.dto.MonederoRecargaResponse;
import com.fitgym.backend.api.dto.MonederoVerifyResponse;
import com.fitgym.backend.api.dto.SocioLoginResponse;
import com.fitgym.backend.api.dto.SocioSession;
import com.fitgym.backend.domain.PagoMonedero;
import com.fitgym.backend.domain.PagoRegistroEstado;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.service.InvalidCredentialsException;
import com.fitgym.backend.service.PagoInitResult;
import com.fitgym.backend.service.PagoMonederoService;
import com.fitgym.backend.service.SocioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monedero")
public class MonederoController {

    private final PagoMonederoService pagoMonederoService;
    private final SocioService socioService;
    private static final String SESSION_SOCIO_KEY = "socioLogin";

    public MonederoController(PagoMonederoService pagoMonederoService, SocioService socioService) {
        this.pagoMonederoService = pagoMonederoService;
        this.socioService = socioService;
    }

    private SocioSession requireSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) throw new InvalidCredentialsException("No hay sesion activa.");

        Object value = session.getAttribute(SESSION_SOCIO_KEY);
        if (!(value instanceof SocioSession socioSession)) throw new InvalidCredentialsException("No hay sesion activa.");

        return socioSession;
    }

    @PostMapping("/recarga")
    public ResponseEntity<MonederoRecargaResponse> recargar(
            @Valid @RequestBody MonederoRecargaRequest req,
            HttpServletRequest request
    ) {
        SocioSession socioSession = requireSession(request);

        PagoInitResult result = pagoMonederoService.iniciarRecarga(socioSession.getId(), req.importe());
        return ResponseEntity.ok(new MonederoRecargaResponse(result.paymentUrl(), result.token()));
    }

    @PostMapping("/verify/{token}")
    public ResponseEntity<MonederoVerifyResponse> verify(
            @PathVariable String token,
            HttpServletRequest request
    ) {
        SocioSession socioSession = requireSession(request);

        PagoMonedero pago = pagoMonederoService.verificarRecarga(token);

        if (pago.getEstado() == PagoRegistroEstado.COMPLETED) {
            Socio socio = socioService.obtenerPorId(socioSession.getId());

            SocioLoginResponse body = new SocioLoginResponse(
                    socio.getId(),
                    socio.getNombre(),
                    socio.getCorreoElectronico(),
                    socio.getEstado().name(),
                    socio.getTarifa().getId(),
                    socio.getTarifa().getNombre(),
                    socio.getSaldoMonedero(),
                    socio.getTelefono(),
                    socio.getDireccion(),
                    socio.getCiudad(),
                    socio.getCodigoPostal()
            );

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute(SESSION_SOCIO_KEY, SocioSession.fromLoginResponse(body));
            }
        }

        return ResponseEntity.ok(new MonederoVerifyResponse(pago.getEstado().name(), pago.getFailureReason()));
    }
}