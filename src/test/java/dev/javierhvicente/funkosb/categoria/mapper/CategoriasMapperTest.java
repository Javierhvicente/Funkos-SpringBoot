package dev.javierhvicente.funkosb.categoria.mapper;

import dev.javierhvicente.funkosb.categoria.dto.CategoriaDto;
import dev.javierhvicente.funkosb.categoria.models.Categoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoriasMapperTest {
    private final Categoria categoria = new Categoria(1L, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true);
    private final CategoriaMapper categoriaMapper = new CategoriaMapper();
    private final CategoriaDto categoriaDto = new CategoriaDto("TEST", null);
    @Test
    void fromCategoriaDto(){
        Categoria mappedCategoria = categoriaMapper.fromCategoriaDto(categoriaDto);
        assertAll(
                () -> assertEquals(categoriaDto.tipo(), mappedCategoria.getTipo()),
                () -> assertEquals(categoriaDto.enabled(), mappedCategoria.getEnabled())
        );
    }
}
