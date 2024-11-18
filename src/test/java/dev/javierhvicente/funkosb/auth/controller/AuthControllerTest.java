package dev.javierhvicente.funkosb.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.javierhvicente.funkosb.auth.dto.JwtAuthResponse;
import dev.javierhvicente.funkosb.auth.dto.UserSignInRequest;
import dev.javierhvicente.funkosb.auth.dto.UserSignUpRequest;
import dev.javierhvicente.funkosb.auth.exceptions.AuthSingInInvalid;
import dev.javierhvicente.funkosb.auth.exceptions.UserAuthNameOrEmailExisten;
import dev.javierhvicente.funkosb.auth.exceptions.UserDiferentePasswords;
import dev.javierhvicente.funkosb.auth.service.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    private final String myEndpoint = "funkos/v1/auth";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;


    @Autowired
    public AuthControllerTest(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        mapper.registerModule(new JavaTimeModule());
    }


    @Test
    void signUp() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("Test", "Test", "test", "test@test.com", "test12345", "test12345");
        var jwtAuthResponse = new JwtAuthResponse("token");
        var myLocalEndpoint = myEndpoint + "/signup";
        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenReturn(jwtAuthResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        assertAll("signup",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUp_WhenPasswordsDoNotMatch_ShouldThrowException() {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordComprobacion("password2");
        request.setEmail("test@test.com");
        request.setNombre("Test");
        request.setApellidos("User");

        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserDiferentePasswords("Las contraseñas no coinciden"));

        assertThrows(UserDiferentePasswords.class, () -> authenticationService.signUp(request));

        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUp_WhenUsernameOrEmailAlreadyExist_ShouldThrowException() {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("test@test.com");
        request.setNombre("Test");
        request.setApellidos("User");

        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserAuthNameOrEmailExisten("El usuario con username " + request.getUsername() + " o email " + request.getEmail() + " ya existe"));

        assertThrows(UserAuthNameOrEmailExisten.class, () -> authenticationService.signUp(request));

        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUp_BadRequest_When_Nombre_Apellidos_Email_Username_Empty_ShouldThrowException() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("");
        request.setNombre("");
        request.setApellidos("");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        assertAll("signup",
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Nombre no puede estar")),
                () -> assertTrue(response.getContentAsString().contains("Apellidos no puede ")),
                () -> assertTrue(response.getContentAsString().contains("Username no puede"))
        );
    }

    @Test
    void signIn() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("Test", "Test", "test", "test@test.com", "test12345", "test12345");
        var jwtAuthResponse = new JwtAuthResponse("token");
        var myLocalEndpoint = myEndpoint + "/signin";
        when(authenticationService.signIn(any(UserSignInRequest.class))).thenReturn(jwtAuthResponse);


        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        assertAll("signin",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));
    }


    @Test
    void signIn_Invalid() {
        var myLocalEndpoint = myEndpoint + "/signin";
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("testuser");
        request.setPassword("<PASSWORD>");

        when(authenticationService.signIn(any(UserSignInRequest.class))).thenThrow(new AuthSingInInvalid("Usuario o contraseña incorrectos"));

        assertThrows(AuthSingInInvalid.class, () -> authenticationService.signIn(request));

        verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));
    }


    @Test
    void signIn_BadRequest_When_Username_Password_Empty_ShouldThrowException() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signin";
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("");
        request.setPassword("");
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        assertAll("signin",
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Username no puede"))
        );
    }
}
