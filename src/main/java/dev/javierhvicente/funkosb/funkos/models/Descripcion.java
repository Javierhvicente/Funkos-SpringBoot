package dev.javierhvicente.funkosb.funkos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class Descripcion {
    @JsonProperty("descripcion")
    private String descripcion;
    @JsonProperty("descriptionUpdatedAt")
    private LocalDateTime DescriptionUpdatedAt = LocalDateTime.now();

    public Descripcion() {}

    public Descripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Getters y setters
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
        this.DescriptionUpdatedAt = LocalDateTime.now(); // Actualiza la fecha cada vez que se modifica
    }

    public LocalDateTime getDescriptionUpdatedAt() {
        return DescriptionUpdatedAt;
    }
}
