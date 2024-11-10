package dev.javierhvicente.funkosb.categoria.service;

import dev.javierhvicente.funkosb.categoria.models.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoriasService {
    Page<Categoria> getAllCategorias(Optional<String> tipo, Optional<Boolean> isEnabled, Pageable pageable);
    Categoria getById(Long id);
    Categoria getByTipo(String string);
    Categoria create(Categoria categoria);
    Categoria update(Long id, Categoria categoria);
    Categoria delete(Long id);

}
