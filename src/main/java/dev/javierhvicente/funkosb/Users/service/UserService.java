package dev.javierhvicente.funkosb.Users.service;

import dev.javierhvicente.funkosb.Users.dto.UserInfoResponse;
import dev.javierhvicente.funkosb.Users.dto.UserRequest;
import dev.javierhvicente.funkosb.Users.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable);

    UserInfoResponse findById(Long id);

    UserResponse save(UserRequest userRequest);

    UserResponse update(Long id, UserRequest userRequest);

    void deleteById(Long id);
}
