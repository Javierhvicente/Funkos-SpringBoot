package dev.javierhvicente.funkosb.auth.service;

import dev.javierhvicente.funkosb.Users.models.User;
import dev.javierhvicente.funkosb.auth.dto.JwtAuthResponse;
import dev.javierhvicente.funkosb.auth.dto.UserSignInRequest;
import dev.javierhvicente.funkosb.auth.dto.UserSignUpRequest;
import dev.javierhvicente.funkosb.auth.exceptions.AuthSingInInvalid;
import dev.javierhvicente.funkosb.auth.exceptions.UserAuthNameOrEmailExisten;
import dev.javierhvicente.funkosb.auth.exceptions.UserDiferentePasswords;
import dev.javierhvicente.funkosb.auth.repositories.AuthUsersRepository;
import dev.javierhvicente.funkosb.auth.service.authentication.AuthenticationServiceImpl;
import dev.javierhvicente.funkosb.auth.service.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private AuthUsersRepository authUsersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    public void testSignUp_WhenPasswordsMatch_ShouldReturnToken() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("test@example.com");
        request.setNombre("Test");
        request.setApellidos("User");

        User userStored = new User();
        when(authUsersRepository.save(any(User.class))).thenReturn(userStored);

        String token = "test_token";
        when(jwtService.generateToken(userStored)).thenReturn(token);

        JwtAuthResponse response = authenticationService.signUp(request);

        assertAll("Sign Up",
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authUsersRepository, times(1)).save(any(User.class)),
                () -> verify(jwtService, times(1)).generateToken(userStored)
        );
    }

    @Test
    public void testSignUp_WhenPasswordsDoNotMatch_ShouldThrowException() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password1");
        request.setPasswordComprobacion("password2");
        request.setEmail("test@example.com");
        request.setNombre("Test");
        request.setApellidos("User");

        assertThrows(UserDiferentePasswords.class, () -> authenticationService.signUp(request));
    }

    @Test
    public void testSignUp_WhenUsernameOrEmailAlreadyExist_ShouldThrowException() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("test@example.com");
        request.setNombre("Test");
        request.setApellidos("User");

        when(authUsersRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserAuthNameOrEmailExisten.class, () -> authenticationService.signUp(request));
    }

    @Test
    public void testSignIn_WhenValidCredentials_ShouldReturnToken() {
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User user = new User();
        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        String token = "test_token";
        when(jwtService.generateToken(user)).thenReturn(token);

        JwtAuthResponse response = authenticationService.signIn(request);

        assertAll("Sign In",
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class)),
                () -> verify(authUsersRepository, times(1)).findByUsername(request.getUsername()),
                () -> verify(jwtService, times(1)).generateToken(user)
        );
    }

    @Test
    public void testSignIn_WhenInvalidCredentials_ShouldThrowException() {
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(AuthSingInInvalid.class, () -> authenticationService.signIn(request));
    }
}
