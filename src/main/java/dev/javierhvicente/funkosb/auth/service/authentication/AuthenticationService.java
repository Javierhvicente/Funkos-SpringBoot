package dev.javierhvicente.funkosb.auth.service.authentication;

import dev.javierhvicente.funkosb.auth.dto.JwtAuthResponse;
import dev.javierhvicente.funkosb.auth.dto.UserSignInRequest;
import dev.javierhvicente.funkosb.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}
