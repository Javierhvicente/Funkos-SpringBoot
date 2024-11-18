package dev.javierhvicente.funkosb.users;

import dev.javierhvicente.funkosb.Users.dto.UserInfoResponse;
import dev.javierhvicente.funkosb.Users.dto.UserRequest;
import dev.javierhvicente.funkosb.Users.dto.UserResponse;
import dev.javierhvicente.funkosb.Users.exceptions.UserNotFound;
import dev.javierhvicente.funkosb.Users.models.User;
import dev.javierhvicente.funkosb.Users.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.javierhvicente.funkosb.utils.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserControllerTests {
    private final UserRequest userRequest = UserRequest.builder()
            .nombre("test")
            .apellidos("test")
            .password("test1234")
            .username("test")
            .email("test@test.com")
            .build();
    private final User user = User.builder().id(99L)
            .nombre("test")
            .apellidos("test")
            .password("test1234")
            .username("test")
            .email("test@test.com")
            .build();
    private final UserResponse userResponse = UserResponse.builder()
            .id(99L)
            .nombre("test")
            .apellidos("test")
            .username("test")
            .email("test@test.com")
            .build();
    private final UserInfoResponse userInfoResponse = UserInfoResponse.builder()
            .id(99L)
            .nombre("test")
            .apellidos("test")
            .username("test")
            .email("test@test.com")
            .build();

    private final String myEndpoint = "/funkos/v1/users";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserService usersService;


    @Autowired
    public UserControllerTests(UserService userService) {
        this.usersService = userService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithAnonymousUser
    void NotAuthenticated() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(403, response.getStatus());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void findAll() throws Exception {
        var list = List.of(userResponse);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<UserResponse> page = new PageImpl<>(list);
        when(usersService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<UserResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertAll("findallUsers",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(usersService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }


    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void findById() throws Exception {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        when(usersService.findById(anyLong())).thenReturn(userInfoResponse);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        var res = mapper.readValue(response.getContentAsString(), UserInfoResponse.class);

        // Assert
        assertAll("findById",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(userInfoResponse, res)
        );

        // Verify
        verify(usersService, times(1)).findById(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void findByIdNotFound() throws Exception {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        when(usersService.findById(anyLong())).thenThrow(new UserNotFound("No existe el usuario"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(usersService, times(1)).findById(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void createUser() throws Exception {
        // Localpoint
        var myLocalEndpoint = myEndpoint;

        // Arrange
        when(usersService.save(any(UserRequest.class))).thenReturn(userResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        var res = mapper.readValue(response.getContentAsString(), UserResponse.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(userResponse, res)
        );

        // Verify
        verify(usersService, times(1)).save(any(UserRequest.class));

    }

    // Hay que comprobar cada una de las validaciones

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void createUserBadRequestPasswordMenosDe5Caracteres() throws Exception {
        var myLocalEndpoint = myEndpoint;
        var userRequest = UserRequest.builder()
                .nombre("test")
                .apellidos("test")
                .password("test")
                .username("test")
                .email("test@test.com")
                .build();
        when(usersService.save(any(UserRequest.class))).thenReturn(userResponse);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());

        verify(usersService, times(0)).save(any(UserRequest.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void createUserBadRequestNombreApellidosYTodoEnBlanco() throws Exception {
        var myLocalEndpoint = myEndpoint;
        var userRequest = UserRequest.builder()
                .nombre("")
                .apellidos("")
                .password("test1234")
                .username("test")
                .email("")
                .build();

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }


    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void updateUser() throws Exception {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        when(usersService.update(anyLong(), any(UserRequest.class))).thenReturn(userResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        var res = mapper.readValue(response.getContentAsString(), UserResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(userResponse, res)
        );

        // Verify
        verify(usersService, times(1)).update(anyLong(), any(UserRequest.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void updateUserNotFound() throws Exception {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        when(usersService.update(anyLong(), any(UserRequest.class))).thenThrow(new UserNotFound("No existe el usuario"));

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(usersService, times(1)).update(anyLong(), any(UserRequest.class));
    }

    // Hacer un test para cada una de las validaciones del update

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void deleteUser() throws Exception {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        doNothing().when(usersService).deleteById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertEquals(204, response.getStatus());

        // Verify
        verify(usersService, times(1)).deleteById(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void deleteUserNotFound() throws Exception {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        doThrow(new UserNotFound("No existe el usuario")).when(usersService).deleteById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(usersService, times(1)).deleteById(anyLong());
    }

    @Test
    @WithMockUser(username = "test", roles = {"ADMIN"})
    void me() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/profile";
        when(usersService.findById(anyLong())).thenReturn(userInfoResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
    }

    @Test
    @WithAnonymousUser
    void me_AnonymousUser() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/profile";
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(403, response.getStatus());
    }
}