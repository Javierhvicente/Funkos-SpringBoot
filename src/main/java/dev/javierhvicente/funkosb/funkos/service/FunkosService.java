package dev.javierhvicente.funkosb.funkos.service;

import dev.javierhvicente.funkosb.funkos.models.Funko;

import java.util.List;

public interface FunkosService {
    List<Funko>getAllFunkos();
    Funko getFunkoById(Long id);
    Funko getFunkoByName(String name);
    Funko createFunko(Funko funko);
    Funko updateFunko(Long id, Funko funko);
    Funko deleteFunko(Long id);
}
