package dev.javierhvicente.funkosb.categoria.mapper;

import dev.javierhvicente.funkosb.categoria.dto.CategoriaDto;
import dev.javierhvicente.funkosb.categoria.models.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    public Categoria fromCategoriaDto(CategoriaDto categoriaDto) {
        var categoria = new Categoria();
        categoria.setTipo(categoriaDto.tipo());
        categoria.setEnabled(categoriaDto.enabled());
        return categoria;
    }

}
