package dev.javierhvicente.funkosb.categoria.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.javierhvicente.funkosb.categoria.dto.CategoriaDto;
import dev.javierhvicente.funkosb.categoria.exceptions.CategoriaException;
import dev.javierhvicente.funkosb.categoria.mapper.CategoriaMapper;
import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.categoria.service.CategoriasService;
import dev.javierhvicente.funkosb.funkos.exceptions.FunkosExceptions;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import dev.javierhvicente.funkosb.utils.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
public class CategoriaControllerTest {
    private final String myEndpoint = "/funkos/v1/categorias";
    private final Categoria categoria = new Categoria(1L, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true);
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private CategoriasService categoriasService;
    @MockBean
    private CategoriaMapper mapperCategorias;
    @Autowired
    private JacksonTester<CategoriaDto> categoriaDto;

    @Autowired
    public CategoriaControllerTest(CategoriasService categoriasService, CategoriaMapper mapperCategorias){
        this.categoriasService = categoriasService;
        this.mapperCategorias = mapperCategorias;
        mapper.registerModule(new JavaTimeModule());
    }
    @Test
    void getAllCategorias() throws Exception {
        var listaCategorias = List.of(categoria);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaCategorias);
        when(categoriasService.getAllCategorias(Optional.empty(), Optional.empty(), pageable)).thenReturn(page);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<Categoria> res =mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );
        verify(categoriasService, times(1)).getAllCategorias(Optional.empty(), Optional.empty(), pageable);
    }
    @Test
    void getAllCategoriasByTipo() throws Exception {
        var listaCategorias = List.of(categoria);
        var localEndPoint = myEndpoint + "?tipo=TEST";
        Optional<String> tipo = Optional.of("TEST");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaCategorias);
        when(categoriasService.getAllCategorias(tipo, Optional.empty(), pageable)).thenReturn(page);
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<Categoria> res =mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );
        verify(categoriasService, times(1)).getAllCategorias(tipo, Optional.empty(), pageable);
    }
    @Test
    void getAllCategoriasByEnabled() throws Exception {
        var listaCategorias = List.of(categoria);
        var localEndPoint = myEndpoint + "?isEnabled=true";
        Optional<Boolean> enabled = Optional.of(true);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaCategorias);
        when(categoriasService.getAllCategorias(Optional.empty(), enabled, pageable)).thenReturn(page);
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<Categoria> res =mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );
        verify(categoriasService, times(1)).getAllCategorias(Optional.empty(), enabled, pageable);
    }
    @Test
    void getAllCategoriasByEnabledAndTipo() throws Exception {
        var listaCategorias = List.of(categoria);
        var localEndPoint = myEndpoint + "?tipo=TEST&isEnabled=true";
        Optional<String> tipo = Optional.of("TEST");
        Optional<Boolean> enabled = Optional.of(true);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaCategorias);
        when(categoriasService.getAllCategorias(tipo, enabled, pageable)).thenReturn(page);
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<Categoria> res =mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );
        verify(categoriasService, times(1)).getAllCategorias(tipo, enabled, pageable);
    }

    @Test
    void getById() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        when(categoriasService.getById(anyLong())).thenReturn(categoria);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Categoria res =mapper.readValue(response.getContentAsString(), Categoria.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoria.getId(), res.getId()),
                () -> assertEquals(categoria.getTipo(), res.getTipo())
        );
        // Verify
        verify(categoriasService, times(1)).getById(anyLong());
    }

    @Test
    void getByIdNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        when(categoriasService.getById(anyLong())).thenThrow(new CategoriaException.CategoriaNotFound(categoria.getId()));
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        // Verify
        verify(categoriasService, times(1)).getById(anyLong());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategoria() throws Exception {
        var categoriaDto = new CategoriaDto("TEST", true);
        when(mapperCategorias.fromCategoriaDto(categoriaDto)).thenReturn(categoria);
        when(categoriasService.create(categoria)).thenReturn(categoria);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(201, response.getStatus())
        );
        // Verify
        verify(categoriasService, times(1)).create(categoria);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategoriaBadRequestEnabled() throws Exception {
        var categoriaDto = new CategoriaDto("TEST", null);
        when(mapperCategorias.fromCategoriaDto(categoriaDto)).thenReturn(categoria);
        when(categoriasService.create(categoria)).thenReturn(categoria);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategoriaBadRequestNoTipo() throws Exception {
        var categoriaDto = new CategoriaDto("", null);
        when(mapperCategorias.fromCategoriaDto(categoriaDto)).thenReturn(categoria);
        when(categoriasService.create(categoria)).thenReturn(categoria);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategoria() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var categoriaDtoUpdated = new CategoriaDto("updated", true);
        var categoriaUpdated = new Categoria(1L, "updated", null, LocalDateTime.now(), LocalDateTime.now(), true);
        when(mapperCategorias.fromCategoriaDto(categoriaDtoUpdated)).thenReturn(categoriaUpdated);
        when(categoriasService.update(anyLong(), any())).thenReturn(categoriaUpdated);
        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(categoriaDto.write(categoriaDtoUpdated).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Categoria res =mapper.readValue(response.getContentAsString(), Categoria.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoriaUpdated.getTipo(), res.getTipo()),
                () -> assertEquals(categoriaUpdated.getEnabled(), res.getEnabled())
        );
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategoriaNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var categoriaDtoUpdated = new CategoriaDto("updated", true);
        var categoriaUpdated = new Categoria(1L, "updated", null, LocalDateTime.now(), LocalDateTime.now(), true);
        when(mapperCategorias.fromCategoriaDto(categoriaDtoUpdated)).thenReturn(categoriaUpdated);
        when(categoriasService.update(anyLong(), any())).thenThrow(new CategoriaException.CategoriaNotFound(categoria.getId()));
        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(categoriaDto.write(categoriaDtoUpdated).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategoriaDtoBadRequestEnabled() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var categoriaUpdted = new CategoriaDto("updated", null);
        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(categoriaDto.write(categoriaUpdted).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategoriaDtoBadRequestTipo() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var categoriaUpdted = new CategoriaDto("", true);
        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(categoriaDto.write(categoriaUpdted).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategoria() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        Categoria categoriaDelete = new Categoria(1L, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true);
        when(categoriasService.delete(anyLong())).thenReturn(categoriaDelete);
        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(204, response.getStatus())
        );
        verify(categoriasService, times(1)).delete(anyLong());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategoriaNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        when(categoriasService.delete(anyLong())).thenThrow(new CategoriaException.CategoriaNotFound(categoria.getId()));
        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(categoriasService, times(1)).delete(anyLong());
    }
}
