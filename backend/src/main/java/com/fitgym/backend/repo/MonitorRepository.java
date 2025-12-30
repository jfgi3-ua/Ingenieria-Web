package com.fitgym.backend.repo;

import com.fitgym.backend.domain.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitorRepository extends JpaRepository<Monitor, Long>{
    
}
