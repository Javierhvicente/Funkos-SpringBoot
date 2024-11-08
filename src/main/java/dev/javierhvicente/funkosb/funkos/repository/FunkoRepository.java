package dev.javierhvicente.funkosb.funkos.repository;

import dev.javierhvicente.funkosb.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FunkoRepository extends JpaRepository<Funko, Long> {
    Funko findByName(String name);
}
