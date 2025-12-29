package com.fitgym.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitgym.backend.repo.ActividadRepository;
import com.fitgym.backend.api.dto.ActividadResponse;

@Service
public class ActividadService {
    
    private final ActividadRepository actividadRepository;

    public ActividadService(ActividadRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    //Metodo para recuperar todas las actividades que hay disponibles
    @Transactional(readOnly = true)
    public List<ActividadResponse> recuperarTodasActividades() {
        return actividadRepository.findAll()
            .stream()
            .map(actividad -> new ActividadResponse(
                actividad.getId(),
                actividad.getNombre(),
                actividad.getHoraIni(),
                actividad.getHoraFin(),
                actividad.getPrecioExtra(),
                actividad.getFecha(),
                actividad.getPlazas(),
                actividad.getMonitor().getNombre()
            ))
            .toList();
    }

}
