package dev.javierhvicente.funkosb.Users.mappers;

import dev.javierhvicente.funkosb.Users.dto.UserInfoResponse;
import dev.javierhvicente.funkosb.Users.dto.UserRequest;
import dev.javierhvicente.funkosb.Users.dto.UserResponse;
import dev.javierhvicente.funkosb.Users.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersMapper {
    public User toUser(UserRequest request) {
        return User.builder()
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles(request.getRoles())
                .isDeleted(request.getIsDeleted())
                .build();
    }

    public User toUser(UserRequest request, Long id) {
        return User.builder()
                .id(id)
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles(request.getRoles())
                .isDeleted(request.getIsDeleted())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .isDeleted(user.getIsDeleted())
                .build();
    }

    public UserInfoResponse toUserInfoResponse(User user, List<String> pedidos) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .isDeleted(user.getIsDeleted())
                .pedidos(pedidos)
                .build();
    }
}
