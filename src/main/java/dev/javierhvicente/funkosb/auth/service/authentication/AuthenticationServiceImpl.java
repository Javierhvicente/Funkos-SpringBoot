package dev.javierhvicente.funkosb.auth.service.authentication;

import Users.models.Role;
import Users.models.User;
import dev.javierhvicente.funkosb.auth.dto.JwtAuthResponse;
import dev.javierhvicente.funkosb.auth.dto.UserSignInRequest;
import dev.javierhvicente.funkosb.auth.dto.UserSignUpRequest;
import dev.javierhvicente.funkosb.auth.exceptions.AuthSingInInvalid;
import dev.javierhvicente.funkosb.auth.exceptions.UserAuthNameOrEmailExisten;
import dev.javierhvicente.funkosb.auth.exceptions.UserDiferentePasswords;
import dev.javierhvicente.funkosb.auth.repositories.AuthUsersRepository;
import dev.javierhvicente.funkosb.auth.service.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthUsersRepository authUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public AuthenticationServiceImpl(AuthUsersRepository authUsersRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.authUsersRepository = authUsersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public JwtAuthResponse signUp(UserSignUpRequest request) {
        log.info("Creando usuario: {}", request);
        if(request.getPassword().contentEquals(request.getPasswordComprobacion())) {
           User user = User.builder()
                   .username(request.getUsername())
                   .password(passwordEncoder.encode(request.getPassword()))
                   .email(request.getEmail())
                   .nombre(request.getNombre())
                   .apellidos(request.getApellidos())
                   .roles(Stream.of(Role.USER).collect(Collectors.toSet()))
                   .build();
           try {
                var unStored = authUsersRepository.save(user);
                return JwtAuthResponse.builder().token(jwtService.generateToken(unStored)).build();
           } catch (Exception e) {
                log.error("Error al crear el usuario: {}", e.getMessage());
               throw new UserAuthNameOrEmailExisten("El usuario con username " + request.getUsername() + " o email " + request.getEmail() + " ya existe");
           }
        }else{
            throw new UserDiferentePasswords("Las contraseñas no coinciden");
        }
    }

    @Override
    public JwtAuthResponse signIn(UserSignInRequest request) {
        log.info("Autenticando usuario: {}", request);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        var user = authUsersRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AuthSingInInvalid("Usuario o contraseña incorrectos"));
        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(jwt).build();
    }
}
