package com.fitgym.backend.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitgym.backend.domain.Actividad;
import com.fitgym.backend.domain.Pago;
import com.fitgym.backend.domain.PagoResultado;
import com.fitgym.backend.domain.Reserva;
import com.fitgym.backend.domain.ReservaEstado;
import com.fitgym.backend.domain.ReservaId;
import com.fitgym.backend.domain.Socio;
import com.fitgym.backend.repo.ActividadRepository;
import com.fitgym.backend.repo.PagoRepository;
import com.fitgym.backend.repo.ReservaRepository;
import com.fitgym.backend.repo.SocioRepository;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final ActividadRepository actividadRepository;
    private final SocioRepository socioRepository;
    private final ActividadService actividadService;
    private final SocioService socioService;
    private final PagoRepository pagoRepository;

    public ReservaService(ReservaRepository reservaRepository, ActividadRepository actividadRepository, SocioRepository socioRepository, ActividadService actividadService, SocioService socioService, PagoRepository pagoRepository) {
        this.reservaRepository = reservaRepository;
        this.actividadRepository = actividadRepository;
        this.socioRepository = socioRepository;
        this.actividadService = actividadService;
        this.socioService = socioService;
        this.pagoRepository = pagoRepository;
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
                //System.out.print("Caso clases gratis");
                //Creamos reserva
                Reserva reserva = new Reserva();
                ReservaId reservaId = new ReservaId(socio.getId(), actividad.getId());
                reserva.setId(reservaId);
                reserva.setSocio(socio);
                reserva.setActividad(actividad);
                reserva.setFecha(OffsetDateTime.now());
                reserva.setEstado(ReservaEstado.CONFIRMADA);    

                //Bajamos disponibles en la activida
                boolean respuesta = actividadService.bajarDisponiblesEnClase(idClase);
                //System.out.print(respuesta);
                
                if(respuesta){
                    reservaRepository.save(reserva);
                }
                else{
                    throw new RuntimeException("No se ha podido terminar la reserva...");
                }

                return respuesta;
            }            
            //Clase de pago incluida en tarifa
            else if(actividad.getPrecioExtra().compareTo(zero) > 0 && socio.getClasesGratis() > 0){
                //System.out.print("Caso clases gratis por incluir en tarifa");
                //Creamos reserva
                Reserva reserva = new Reserva();
                ReservaId reservaId = new ReservaId(socio.getId(), actividad.getId());
                reserva.setId(reservaId);
                reserva.setSocio(socio);
                reserva.setActividad(actividad);
                reserva.setFecha(OffsetDateTime.now());
                reserva.setEstado(ReservaEstado.CONFIRMADA);

                //Bajamos disponibles en la activida y en las clases gratis del socio
                boolean respuestaDisponiblesClase = actividadService.bajarDisponiblesEnClase(idClase);
                boolean respuestaClasesCliente = socioService.disminuirClasesGratisDeTarifa(idSocio);
                boolean respuesta = false;
                //System.out.print(respuestaClasesCliente);
                //System.out.print(respuestaDisponiblesClase);
                if(respuestaClasesCliente && respuestaDisponiblesClase){
                    respuesta = true;
                    reservaRepository.save(reserva);
                }
                else{
                    throw new RuntimeException("No se ha podido terminar la reserva...");
                }
                                
                return respuesta;
            }
            //Clase de pago no incluida en tarifa
            else if(actividad.getPrecioExtra().compareTo(zero) > 0 && socio.getClasesGratis() <= 0){
                //System.out.print("Caso clases pagada");
                //Caso de sin dinero -> alerta en front de que no tiene fondos
                //Caso de con dinero -> Bajar monedero del usuario y confirmar reserva
                if (socio.getSaldoMonedero().compareTo(actividad.getPrecioExtra()) >= 0) {
                    //Bajamos disponibles en la activida y en las clases gratis del socio
                    boolean respuestaDisponiblesClase = actividadService.bajarDisponiblesEnClase(idClase);

                    if(!respuestaDisponiblesClase){
                        return false;
                    }

                    //Bajamos el saldo
                    BigDecimal saldoRestante = socio.getSaldoMonedero().subtract(actividad.getPrecioExtra());
                    socio.setSaldoMonedero(saldoRestante);

                    //Creamos reserva
                    Reserva reserva = new Reserva();
                    ReservaId reservaId = new ReservaId(socio.getId(), actividad.getId());
                    reserva.setId(reservaId);
                    reserva.setSocio(socio);
                    reserva.setActividad(actividad);
                    reserva.setFecha(OffsetDateTime.now());
                    reserva.setEstado(ReservaEstado.CONFIRMADA);
                    reservaRepository.save(reserva);

                    //Guardamos el pago
                    Pago pago = new Pago();
                    pago.setCantidad(actividad.getPrecioExtra());
                    pago.setFechaPago(Instant.now());
                    pago.setIdActividad(idClase);
                    pago.setIdSocio(idSocio);
                    pago.setNombre("Pago reserva actividad");
                    pago.setResultadoPago(PagoResultado.OK);
                    pagoRepository.save(pago);

                    return true;
                }
                else{
                    throw new RuntimeException("Saldo insuficiente");
                }
            }
            else{
                return false;
            }
        }
    }
}
