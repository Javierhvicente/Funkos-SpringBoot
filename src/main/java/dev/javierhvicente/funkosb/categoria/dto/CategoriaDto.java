package dev.javierhvicente.funkosb.categoria.dto;

import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoriaDto(
        @NotBlank(message = "El tipo de la categoría no puede ser vacío")
        String tipo,
        @NotNull
        Boolean enabled
) {
}
