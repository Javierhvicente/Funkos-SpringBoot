package dev.javierhvicente.funkosb.categoria.dto;

import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CategoriaDto(
        @NotBlank(message = "El tipo de la categoría no puede ser vacío")
        String tipo,
        @NotBlank(message = "El enabled de la categoría no puede ser vacio")
        Boolean enabled
) {
}
