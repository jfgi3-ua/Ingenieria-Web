package com.fitgym.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitgym.backend.domain.Actividad;
import com.fitgym.backend.domain.Reserva;
import com.fitgym.backend.domain.ReservaEstado;
import com.fitgym.backend.domain.ReservaId;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.repo.ActividadRepository;
import com.fitgym.backend.repo.ReservaRepository;
import com.fitgym.backend.repo.SocioRepository;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final ActividadRepository actividadRepository;
    private final SocioRepository socioRepository;
    private final ActividadService actividadService;

    public ReservaService(ReservaRepository reservaRepository, ActividadRepository actividadRepository, SocioRepository socioRepository, ActividadService actividadService) {
        this.reservaRepository = reservaRepository;
        this.actividadRepository = actividadRepository;
        this.socioRepository = socioRepository;
        this.actividadService = actividadService;
    }

    //Método para reservar una actividad
    @Transactional
    public boolean reservarClase(Long idClase, Long idSocio) {
        Actividad actividad = actividadRepository.findById(idClase).orElse(null);
        Socio socio = socioRepository.findById(idSocio).orElse(null);

        if(actividad == null){
            throw new ActividadNoEncontradaException("No se ha encontrado la actividad");
        }
        else if(socio == null){
            throw new SocioInactivoException("El cliente no existe o no esta activo...");
        }
        else{
            if(actividad.getDisponibles() <= 0){
                throw new ActividadSinPlazasException("No se pueden meter más usuarios en la actividad. Plazas a 0");
            }
            //Clase gratis
            BigDecimal zero = new BigDecimal("0");
            if(actividad.getPrecioExtra().compareTo(zero) <= 0){
                //Creamos reserva
                Reserva reserva = new Reserva();
                ReservaId reservaId = new ReservaId(socio.getId(), actividad.getId());
                reserva.setId(reservaId);
                reserva.setSocio(socio);
                reserva.setActividad(actividad);
                reserva.setFecha(OffsetDateTime.now());
                reserva.setEstado(ReservaEstado.CONFIRMADA);    

                reservaRepository.save(reserva);

                //Bajamos disponibles en la activida
                boolean respuesta = actividadService.bajarDisponiblesEnClase(idClase);

                return respuesta;
            }
            else {
                return false;
            }
            
            //Clase de pago incluida en tarifa
            /*if(actividad.getPrecioExtra().compareTo(zero) > 0 && socio.getClasesGratis() > 0){
                //Creamos reserva
                Reserva reserva = new Reserva();
                reserva.setSocio(socio);
                reserva.setActividad(actividad);
                reserva.setFecha(LocalDate.now());
                reserva.setEstado(ReservaEstado.CONFIRMADA);

                reservaRepository.save(reserva);

                //Bajamos disponibles en la activida
                boolean respuesta = actividadService.bajarDisponiblesEnClase(idClase);

                return respuesta;
            }*/
            //Clase de pago no incluida en tarifa
        }
    }
}
