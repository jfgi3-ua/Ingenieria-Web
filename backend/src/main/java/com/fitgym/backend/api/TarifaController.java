package com.fitgym.backend.api;

import com.fitgym.backend.domain.Tarifa;
import com.fitgym.backend.repo.TarifaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

  private final TarifaRepository repo;

  public TarifaController(TarifaRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Tarifa> list() {
    return repo.findAll();
  }
}
