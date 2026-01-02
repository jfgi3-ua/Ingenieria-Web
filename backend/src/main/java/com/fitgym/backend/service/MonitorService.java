package com.fitgym.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitgym.backend.domain.Monitor;
import com.fitgym.backend.repo.MonitorRepository;

@Service
public class MonitorService {
    
    private final MonitorRepository monitorRepository;

    public MonitorService(MonitorRepository monitorRepository){
        this.monitorRepository = monitorRepository;
    }

    //Recuperar el nombre del monitor a traves de su id
    @Transactional(readOnly = true)
    public String recuperarNombreMonitor(Long id) {
        Monitor monitor = monitorRepository.findById(id).orElse(null);

        if(monitor == null){
            throw new Error("No se ha encontrado al monitor...");
        }

        return monitor.getNombre();
    }
}
