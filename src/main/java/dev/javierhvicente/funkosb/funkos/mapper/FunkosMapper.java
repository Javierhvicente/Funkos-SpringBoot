package dev.javierhvicente.funkosb.funkos.mapper;

import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.funkos.dto.FunkoDto;
import dev.javierhvicente.funkosb.funkos.models.Descripcion;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import org.springframework.stereotype.Component;

@Component
public class FunkosMapper {

    public Funko fromFunkoDto(FunkoDto dto) {
        var funko = new Funko();
        funko.setName(dto.name());
        funko.setCategoria(new Categoria(dto.categoria())); // Ahora funciona
        funko.setPrice(dto.price());
        funko.setImagen(dto.imagen());
        Descripcion descripcion = new Descripcion(dto.descripcion());
        funko.setDescripcion(descripcion);
        funko.setEnabled(dto.enabled());
        return funko;
    }

}
