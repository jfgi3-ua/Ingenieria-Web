package com.fitgym.backend.api;

import com.fitgym.backend.api.dto.ReservaItemResponse;
import com.fitgym.backend.api.dto.ReservaResponse;
import com.fitgym.backend.api.dto.SocioSession;
import com.fitgym.backend.service.InvalidCredentialsException;
import com.fitgym.backend.service.ReservaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fitgym.backend.api.dto.ReservaCancelResponse;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private static final String SESSION_SOCIO_KEY = "socioLogin";

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    private SocioSession requireSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) throw new InvalidCredentialsException("No hay sesion activa.");

        Object value = session.getAttribute(SESSION_SOCIO_KEY);
        if (!(value instanceof SocioSession socioSession)) {
            throw new InvalidCredentialsException("No hay sesion activa.");
        }

        return socioSession;
    }

    @PostMapping
    public ResponseEntity<Boolean> crearReserva(@RequestBody ReservaResponse request) {

        boolean res = reservaService.reservarClase(request.idActividad(), request.idSocio());
        System.out.print(res);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservaItemResponse>> misReservas(
            @RequestParam(defaultValue = "5") int limit,
            HttpServletRequest request
    ) {
        SocioSession s = requireSession(request);
        return ResponseEntity.ok(reservaService.listarReservasSocioDTO(s.getId(), limit));
    }

    @PostMapping("/me/{idActividad}/cancel")
    public ResponseEntity<ReservaCancelResponse> cancelarReserva(
            @PathVariable Long idActividad,
            HttpServletRequest request
    ) {
        SocioSession s = requireSession(request);
        return ResponseEntity.ok(reservaService.cancelarReserva(s.getId(), idActividad));
    }
}