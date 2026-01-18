package com.fitgym.backend.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

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
import com.fitgym.backend.api.dto.ReservaItemResponse;
import com.fitgym.backend.api.dto.ReservaCancelResponse;
import java.util.Optional;

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
            boolean respuesta = false;
            
            List<Reserva> reservas = reservaRepository.findAll();
            Reserva reservaCancelada = new Reserva();
            for(Reserva reserva : reservas){
                if(reserva.getSocio().getId() == idSocio && reserva.getActividad().getId() == idClase && reserva.getEstado() == ReservaEstado.CONFIRMADA){
                    return respuesta = false;
                }
                else if(reserva.getSocio().getId() == idSocio && reserva.getActividad().getId() == idClase && reserva.getEstado() == ReservaEstado.CANCELADA){
                    reservaCancelada = reserva;
                }
            }

            if(actividad.getPrecioExtra().compareTo(zero) <= 0){
                if(reservaCancelada.getEstado() == ReservaEstado.CANCELADA){
                    reservaCancelada.setEstado(ReservaEstado.CONFIRMADA);

                    respuesta = actividadService.bajarDisponiblesEnClase(idClase);
                }
                else{
                    //Creamos reserva
                    Reserva reserva = new Reserva();
                    ReservaId reservaId = new ReservaId(socio.getId(), actividad.getId());
                    reserva.setId(reservaId);
                    reserva.setSocio(socio);
                    reserva.setActividad(actividad);
                    reserva.setFecha(OffsetDateTime.now());
                    reserva.setEstado(ReservaEstado.CONFIRMADA); 

                    //Bajamos disponibles en la activida
                    respuesta = actividadService.bajarDisponiblesEnClase(idClase);
                    
                    if(respuesta){
                        reservaRepository.save(reserva);
                    }
                    else{
                        throw new RuntimeException("No se ha podido terminar la reserva...");
                    }
                }

                return respuesta;
            }            
            //Clase de pago incluida en tarifa
            else if(actividad.getPrecioExtra().compareTo(zero) > 0 && socio.getClasesGratis() > 0){
                //System.out.print("Caso clases gratis por incluir en tarifa");
                //Creamos reserva y se hace el pago(aunque sea quitar la clase gratuita)
                Reserva reserva = new Reserva();
                ReservaId reservaId = new ReservaId(socio.getId(), actividad.getId());
                reserva.setId(reservaId);
                reserva.setSocio(socio);
                reserva.setActividad(actividad);
                reserva.setFecha(OffsetDateTime.now());
                reserva.setEstado(ReservaEstado.CONFIRMADA);

                Pago pago = new Pago();
                //pago.setCantidad(actividad.getPrecioExtra());
                pago.setCantidad(BigDecimal.ZERO);
                pago.setNombre("Reserva con clase gratis");
                pago.setFechaPago(Instant.now());
                pago.setIdActividad(idClase);
                pago.setIdSocio(idSocio);
                pago.setNombre("Pago reserva actividad");
                pago.setResultadoPago(PagoResultado.OK);

                //Bajamos disponibles en la activida y en las clases gratis del socio
                boolean respuestaDisponiblesClase = actividadService.bajarDisponiblesEnClase(idClase);
                boolean respuestaClasesCliente = socioService.disminuirClasesGratisDeTarifa(idSocio);
                //boolean respuesta = false;
                //System.out.print(respuestaClasesCliente);
                //System.out.print(respuestaDisponiblesClase);
                if(respuestaClasesCliente && respuestaDisponiblesClase){
                    respuesta = true;
                    reservaRepository.save(reserva);
                    pagoRepository.save(pago);
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
                    socioRepository.save(socio);

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

    @Transactional(readOnly = true)
    public List<ReservaItemResponse> listarReservasSocioDTO(Long socioId, int limit) {
        var list = reservaRepository.findBySocioOrderByFechaDesc(socioId);

        return list.stream()
                .limit(Math.max(0, limit))
                .map(r -> {
                    var a = r.getActividad();

                    BigDecimal precioPagado = pagoRepository.findBySocioAndActividad(socioId, a.getId())
                            .filter(p -> p.getResultadoPago() == PagoResultado.OK)
                            .map(Pago::getCantidad)
                            .orElse(BigDecimal.ZERO);

                    return new ReservaItemResponse(
                            a.getId(),
                            a.getNombre(),
                            a.getFecha(),
                            a.getHoraIni(),
                            a.getHoraFin(),
                            r.getEstado().name(),
                            precioPagado
                    );
                })
                .toList();
    }

    @Transactional
    public ReservaCancelResponse cancelarReserva(Long socioId, Long idActividad) {

        ReservaId rid = new ReservaId(socioId, idActividad);
        Reserva reserva = reservaRepository.findById(rid)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // si ya está cancelada, devolvemos saldo actual sin cambios
        if (reserva.getEstado() == ReservaEstado.CANCELADA) {
            Socio socio = socioRepository.findById(socioId)
                    .orElseThrow(() -> new RuntimeException("Socio no encontrado"));
            return new ReservaCancelResponse("CANCELADA", BigDecimal.ZERO, socio.getSaldoMonedero());
        }

        // marcar cancelada
        reserva.setEstado(ReservaEstado.CANCELADA);
        reservaRepository.save(reserva);

        // liberar plaza en actividad
        Actividad actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));
        actividad.setDisponibles(actividad.getDisponibles() + 1);
        actividadRepository.save(actividad);

        // calcular reembolso: SOLO si hubo pago real (>0)
        BigDecimal reembolso = BigDecimal.ZERO;

        Optional<Pago> pagoOpt = pagoRepository.findBySocioAndActividad(socioId, idActividad);
        if (pagoOpt.isPresent()) {
            Pago p = pagoOpt.get();
            if (p.getResultadoPago() == PagoResultado.OK && p.getCantidad().compareTo(BigDecimal.ZERO) > 0) {
                reembolso = p.getCantidad();
            }
        }

        // aplicar reembolso al monedero si procede
        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado"));

        if (reembolso.compareTo(BigDecimal.ZERO) > 0) {
            socio.setSaldoMonedero(socio.getSaldoMonedero().add(reembolso));
            socioRepository.save(socio);
        }

        return new ReservaCancelResponse("CANCELADA", reembolso, socio.getSaldoMonedero());
    }
}
