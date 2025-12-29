package com.fitgym.backend.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitgym.backend.api.dto.ActividadResponse;
import com.fitgym.backend.service.ActividadService;

@RestController
@RequestMapping("/api/actividades")
public class ActividadController {
    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService){
        this.actividadService = actividadService;
    }

    @GetMapping("/servicios")
    public List<ActividadResponse> obtenerActividades() {
        return actividadService.recuperarTodasActividades();
    }
}
