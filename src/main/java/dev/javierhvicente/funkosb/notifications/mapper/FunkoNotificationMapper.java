package dev.javierhvicente.funkosb.notifications.mapper;

import dev.javierhvicente.funkosb.funkos.models.Funko;
import dev.javierhvicente.funkosb.notifications.dto.FunkoNotificationDto;
import org.springframework.stereotype.Component;

@Component
public class FunkoNotificationMapper {
    public FunkoNotificationDto toFunkoNotificationDto(Funko funko){
        return new FunkoNotificationDto(
                funko.getId(),
                funko.getName(),
                funko.getDescripcion().getDescripcion(),
                funko.getCategoria().getTipo(),
                funko.getImagen(),
                funko.getPrice(),
                funko.getCreatedAt().toString(),
                funko.getUpdatedAt().toString()
        );
    }

}
