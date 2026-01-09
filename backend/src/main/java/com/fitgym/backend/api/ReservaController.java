package com.fitgym.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitgym.backend.api.dto.ReservaResponse;
import com.fitgym.backend.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<Void> crearReserva(@RequestBody ReservaResponse request) {

        reservaService.reservarClase(request.idActividad(), request.idSocio());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
