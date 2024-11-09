package dev.javierhvicente.funkosb.funkos.repository;

import dev.javierhvicente.funkosb.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FunkoRepository extends JpaRepository<Funko, Long>, JpaSpecificationExecutor<Funko> {
    Funko findByName(String name);
}
