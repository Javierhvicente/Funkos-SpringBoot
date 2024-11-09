package dev.javierhvicente.funkosb.notifications.dto;

public record FunkoNotificationDto(
    Long id,
    String name,
    String description,
    String categoria,
    String imageUrl,
    Double price,
    String createdAt,
    String updatedAt

) {
}
