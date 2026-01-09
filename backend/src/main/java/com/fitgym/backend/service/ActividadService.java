package com.fitgym.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitgym.backend.repo.ActividadRepository;
import com.fitgym.backend.repo.SocioRepository;
import com.fitgym.backend.api.dto.ActividadResponse;
import com.fitgym.backend.domain.Actividad;
import com.fitgym.backend.domain.Reserva;
import com.fitgym.backend.domain.ReservaEstado;
import com.fitgym.backend.domain.Socio;

@Service
public class ActividadService {
    
    private final ActividadRepository actividadRepository;
    private final SocioRepository socioRepository;
     
    public ActividadService(ActividadRepository actividadRepository, SocioRepository socioRepository) {
        this.actividadRepository = actividadRepository;
        this.socioRepository = socioRepository;
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
                actividad.getDisponibles(),
                actividad.getMonitor().getNombre()
            ))
            .toList();
    }

    //Método para reservar una actividad
    @Transactional
    public boolean bajarDisponiblesEnClase(Long idClase) {
        Actividad actividad = actividadRepository.findById(idClase).orElse(null);

        if(actividad == null){
            throw new ActividadNoEncontradaException("No se ha encontrado la actividad");
        }

        Integer totalDisponibles = actividad.getDisponibles();
    
        if (totalDisponibles > 1) {
            actividad.setDisponibles(totalDisponibles - 1);
            return true;
        }
        else{
            return false;
        }
    }
}
