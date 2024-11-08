package dev.javierhvicente.funkosb.categoria.repository;

import dev.javierhvicente.funkosb.categoria.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoriasRepository extends JpaRepository<Categoria, Long> {
    Categoria findByTipo(String string);
}
