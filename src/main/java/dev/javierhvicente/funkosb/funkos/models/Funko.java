package dev.javierhvicente.funkosb.funkos.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.javierhvicente.funkosb.categoria.models.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "Funkos")
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Funko {
    public static final Long DEFAULT_ID = 0L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Se genera automáticamente el ID en la base de datos (Opcional)
    private Long id = DEFAULT_ID;

    @Column(nullable = false, length = 100)
    @NotEmpty // Indicamos que no puede ser vacío
    private String name;
    @Embedded
    private Descripcion descripcion;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonIgnoreProperties("funkos")
    private Categoria categoria;
    @Column
    private String imagen;
    @Column(nullable = false)
    @DecimalMin(value = "10.99", message = "El precio no puede ser menor a 10.99")
    @DecimalMax(value = "59.99", message = "El precio no puede ser mayor a 59.99")
    private Double price;
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(columnDefinition = "integer default 0")
    @Builder.Default
    private Integer stock = 0;

    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean enabled = true;

}