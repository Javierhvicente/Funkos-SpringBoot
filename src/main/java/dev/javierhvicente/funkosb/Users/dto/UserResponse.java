package dev.javierhvicente.funkosb.Users.dto;

import dev.javierhvicente.funkosb.Users.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String username;
    private String email;
    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);
    @Builder.Default
    private Boolean isDeleted = false;
}
