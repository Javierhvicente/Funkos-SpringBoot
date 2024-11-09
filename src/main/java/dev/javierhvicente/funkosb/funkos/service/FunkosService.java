package dev.javierhvicente.funkosb.funkos.service;

import dev.javierhvicente.funkosb.funkos.models.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FunkosService {
    Page<Funko> getAllFunkos(Optional<String> categoria, Optional<String> name, Optional<String> description, Optional<Double> minPrice, Optional<Double> maxPrice, Optional<Boolean> isEnabled, Pageable pageable);
    Funko getFunkoById(Long id);
    Funko getFunkoByName(String name);
    Funko createFunko(Funko funko);
    Funko updateFunko(Long id, Funko funko);
    Funko deleteFunko(Long id);
}
