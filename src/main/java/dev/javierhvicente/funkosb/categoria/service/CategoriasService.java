package dev.javierhvicente.funkosb.categoria.service;

import dev.javierhvicente.funkosb.categoria.models.Categoria;

import java.util.List;

public interface CategoriasService {
    List<Categoria> getCategorias();
    Categoria getById(Long id);
    Categoria getByTipo(String string);
    Categoria create(Categoria categoria);
    Categoria update(Long id, Categoria categoria);
    Categoria delete(Long id);

}
