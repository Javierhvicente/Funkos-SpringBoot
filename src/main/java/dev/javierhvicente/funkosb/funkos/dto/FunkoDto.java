package dev.javierhvicente.funkosb.funkos.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public record  FunkoDto(
    @NotBlank(message = "El nombre no puede estar vacío")
    String name,

    @NotBlank(message = "El categoria no puede estar vacio")
    String categoria,
    @NotBlank(message = "La descripcion no puede estar vacia")
    String descripcion,
    String imagen,
    @DecimalMin(value ="10.99", message = "El precio no puede ser menor a 10.99")
    @DecimalMax(value ="59.99", message = "El precio no puede ser mayor a 59.99")
    Double price,
    @Min(value = 0, message = "El stock no puede ser negativo")
    Integer stock,
    Boolean enabled
) {
    
}
