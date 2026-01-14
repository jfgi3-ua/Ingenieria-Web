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
import com.fitgym.backend.api.dto.ActividadAdminRequest;
import com.fitgym.backend.api.dto.ActividadAdminResponse;
import com.fitgym.backend.domain.Monitor;
import com.fitgym.backend.domain.Sala;
import com.fitgym.backend.domain.TipoActividad;
import com.fitgym.backend.repo.MonitorRepository;
import com.fitgym.backend.repo.SalaRepository;
import com.fitgym.backend.repo.TipoActividadRepository;

@Service
public class ActividadService {
    
    private final ActividadRepository actividadRepository;
    private final SocioRepository socioRepository;
    private final MonitorRepository monitorRepository;
    private final SalaRepository salaRepository;
    private final TipoActividadRepository tipoActividadRepository;

     
    public ActividadService(
        ActividadRepository actividadRepository,
        SocioRepository socioRepository,
        MonitorRepository monitorRepository,
        SalaRepository salaRepository,
        TipoActividadRepository tipoActividadRepository
    ) {
            this.actividadRepository = actividadRepository;
            this.socioRepository = socioRepository;
            this.monitorRepository = monitorRepository;
            this.salaRepository = salaRepository;
            this.tipoActividadRepository = tipoActividadRepository;
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
                actividad.getMonitor().getNombre(),
                actividad.getSala().getDescripcion(),
                actividad.getSala().getFoto(),
                actividad.getTipoActividad().getNombre()
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
        
            if (totalDisponibles >= 1) {
                actividad.setDisponibles(totalDisponibles - 1);
                return true;
            }
            else{
                return false;
            }
        }
        @Transactional(readOnly = true)
    public List<ActividadAdminResponse> adminListar() {
    return actividadRepository.findAll().stream()
        .map(this::toAdminResponse)
        .toList();
    }

    @Transactional
    public ActividadAdminResponse adminCrear(ActividadAdminRequest req) {
    Monitor monitor = monitorRepository.findById(req.idMonitor)
        .orElseThrow(() -> new IllegalArgumentException("Monitor no encontrado: " + req.idMonitor));

    Sala sala = salaRepository.findById(req.idSala)
        .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada: " + req.idSala));

    TipoActividad tipo = tipoActividadRepository.findById(req.idTipoActividad)
        .orElseThrow(() -> new IllegalArgumentException("TipoActividad no encontrado: " + req.idTipoActividad));

    Actividad a = new Actividad();
    a.setNombre(req.nombre);
    a.setHoraIni(req.horaIni);
    a.setHoraFin(req.horaFin);
    a.setPrecioExtra(req.precioExtra);
    a.setFecha(req.fecha);
    a.setPlazas(req.plazas);

    // al crear: disponibles = plazas
    a.setDisponibles(req.plazas);

    a.setMonitor(monitor);
    a.setSala(sala);
    a.setTipoActividad(tipo);

    Actividad saved = actividadRepository.save(a);
    return toAdminResponse(saved);
    }

    @Transactional
    public ActividadAdminResponse adminEditar(Long id, ActividadAdminRequest req) {
    Actividad a = actividadRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada: " + id));

    Monitor monitor = monitorRepository.findById(req.idMonitor)
        .orElseThrow(() -> new IllegalArgumentException("Monitor no encontrado: " + req.idMonitor));

    Sala sala = salaRepository.findById(req.idSala)
        .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada: " + req.idSala));

    TipoActividad tipo = tipoActividadRepository.findById(req.idTipoActividad)
        .orElseThrow(() -> new IllegalArgumentException("TipoActividad no encontrado: " + req.idTipoActividad));

    // Ajuste de disponibles si cambian plazas:
    // mantenemos el número de reservas ya hechas
    int reservadas = a.getPlazas() - a.getDisponibles();
    a.setPlazas(req.plazas);
    a.setDisponibles(Math.max(req.plazas - reservadas, 0));

    a.setNombre(req.nombre);
    a.setHoraIni(req.horaIni);
    a.setHoraFin(req.horaFin);
    a.setPrecioExtra(req.precioExtra);
    a.setFecha(req.fecha);

    a.setMonitor(monitor);
    a.setSala(sala);
    a.setTipoActividad(tipo);
    
    actividadRepository.save(a);
    return toAdminResponse(a);
    }

    private ActividadAdminResponse toAdminResponse(Actividad a) {
    // fuerza carga lazy
    a.getMonitor().getNombre();
    a.getSala().getDescripcion();
    a.getTipoActividad().getNombre();

    return new ActividadAdminResponse(
        a.getId(),
        a.getNombre(),
        a.getHoraIni(),
        a.getHoraFin(),
        a.getPrecioExtra(),
        a.getFecha(),
        a.getPlazas(),
        a.getDisponibles(),
        a.getMonitor().getId(),
        a.getMonitor().getNombre(),
        a.getSala().getId(),
        a.getSala().getDescripcion(),
        a.getSala().getFoto(),
        a.getTipoActividad().getId(),
        a.getTipoActividad().getNombre()
    );
    }

}
