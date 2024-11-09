package dev.javierhvicente.funkosb.funkos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.funkos.dto.FunkoDto;
import dev.javierhvicente.funkosb.funkos.exceptions.FunkosExceptions;
import dev.javierhvicente.funkosb.funkos.mapper.FunkosMapper;
import dev.javierhvicente.funkosb.funkos.models.Descripcion;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import dev.javierhvicente.funkosb.funkos.service.FunkosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
public class FunkoControllerTest {
    private final String myEndpoint = "/funkos/v1/funkos";
    private final Descripcion descripcion = new Descripcion("SoyTest");
    private final Categoria categoria = new Categoria(1L, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko = new Funko(1L, "FunkoTest", descripcion, categoria,"soy.png", 19.99 , LocalDateTime.now(), LocalDateTime.now(), true);


    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private FunkosService funkosService;
    @MockBean
    private FunkosMapper mapperFunko;
    @Autowired
    private JacksonTester<FunkoDto> funkoDto;
    @Autowired
    public FunkoControllerTest(FunkosService funkosService, FunkosMapper funkosMapperFunko){
        this.funkosService = funkosService;
        this.mapperFunko = funkosMapperFunko;
        mapper.registerModule(new JavaTimeModule());

    }
    /*
    @Test
    void getAllFunkos() throws Exception {
        // Arrange
        when(funkosService.getAllFunkos()).thenReturn(List.of(funko));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Funko> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Funko.class));

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus())

        );

        // Verify
        verify(funkosService, times(1)).getAllFunkos();
    }
    */
    @Test
    void getFunkoById() throws Exception {
        var myLocalEndpoint = "/funkos/v1/funkos/1";  // Verifica la URL

        // Arrange
        when(funkosService.getFunkoById(anyLong())).thenReturn(funko);  // Asegúrate de que `funko` no sea null

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

            // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko.getId(), res.getId()),
                () -> assertEquals(funko.getName(), res.getName()),
                () -> assertEquals(funko.getDescripcion().getDescripcion(), res.getDescripcion().getDescripcion())
        );
        // Verify
        verify(funkosService, times(1)).getFunkoById(anyLong());
    }


    @Test
    void getProductByIdNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        when(funkosService.getFunkoById(anyLong())).thenThrow(new FunkosExceptions.FunkoNotFound(funko.getId()));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(funkosService, times(1)).getFunkoById(anyLong());
    }

    @Test
    void getFunkoByName() throws Exception {
        var myLocalEndpoint = "/funkos/v1/funkos/nombre/FunkoTest";  // Asegúrate de que la URL es correcta;
        when(funkosService.getFunkoByName(funko.getName())).thenReturn(funko);  // Mock del servicio

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);

            // Assert
            assertAll(
                    () -> assertEquals(200, response.getStatus()),
                    () -> assertEquals(funko.getName(), res.getName())
            );
        // Verify
        verify(funkosService, times(1)).getFunkoByName(funko.getName());
    }


    @Test
    void getFunkoByNameNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/nombre/test";

        // Arrange
        when(funkosService.getFunkoByName("test")).thenThrow(new FunkosExceptions.FunkoNotFoundByName(funko.getName()));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(funkosService, times(1)).getFunkoByName("test");
    }

    @Test
    void createFunko() throws Exception {
        var funkoDto = new FunkoDto(
                "Funko test",
                "TEST",
                "SoyTest",
                "soy.png",
                19.99,
                true
        );

        when(mapperFunko.fromFunkoDto(funkoDto)).thenReturn(funko);
        when(funkosService.createFunko(funko)).thenReturn(funko);

        MockHttpServletResponse response = mockMvc.perform(
                        post("/funkos/v1/funkos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(201, response.getStatus())
        );

        verify(funkosService, times(1)).createFunko(funko);
    }



    @Test
    void createFunkoBadRequest() throws Exception {
        var funkoNew = new FunkoDto("Funko test", "TEST", "SoyTest", "soy.png", 199.99, true);
        when(funkosService.createFunko(any(Funko.class))).thenReturn(funko);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(funkoDto.write(funkoNew).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void updateFunko() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var funkoUpdated = new FunkoDto("Funko test updated", "TEST", "SoyTestUpdated", "soy.png", 29.99, true);
        var Newcategoria = new Categoria("TEST");
        var Newdescripcion = new Descripcion("SoyTestUpdated");
        var newFunko = new Funko(1L, "Funko test updated", Newdescripcion, Newcategoria, "soy.png", 29.99, LocalDateTime.now(), LocalDateTime.now(), true );
        when(mapperFunko.fromFunkoDto(funkoUpdated)).thenReturn(newFunko);
        when(funkosService.updateFunko(anyLong(), any(Funko.class))).thenReturn(newFunko);
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(funkoDto.write(funkoUpdated).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Funko res = mapper.readValue(response.getContentAsString(), Funko.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(newFunko.getName(), res.getName()),
                () -> assertEquals(newFunko.getDescripcion().getDescripcion(), res.getDescripcion().getDescripcion())
        );
        verify(funkosService, times(1)).updateFunko(anyLong(), any(Funko.class));
    }

    @Test
    void updateFunkoNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/99999";
        var funkoUpdated = new FunkoDto("Funko test updated", "TEST", "SoyTestUpdated", "soy.png", 29.99, true);
        var Newcategoria = new Categoria("TEST");
        var Newdescripcion = new Descripcion("SoyTestUpdated");
        var newFunko = new Funko(null, "Funko test updated", Newdescripcion, Newcategoria, "soy.png", 29.99, LocalDateTime.now(), LocalDateTime.now(), true );
        when(mapperFunko.fromFunkoDto(funkoUpdated)).thenReturn(newFunko);
        when(funkosService.updateFunko(anyLong(), any(Funko.class)))
                .thenThrow(new FunkosExceptions.FunkoNotFound(anyLong()));


        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(funkoDto.write(funkoUpdated).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
    }

    @Test
    void updateFunkoBadRequest() throws Exception{
        var myLocalEndpoint = myEndpoint + "/1";
        var funkoUpdated = new FunkoDto("Funko test updated", "TEST", "SoyTestUpdated", "soy.png", 229.99, true);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(funkoDto.write(funkoUpdated).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());
    }

    @Test
    void deleteFunko() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        when(funkosService.deleteFunko(anyLong())).thenReturn(funko);
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(204, response.getStatus())
        );
        verify(funkosService, times(1)).deleteFunko(anyLong());
    }

    @Test
    void deleteFunkoNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/99999";
        when(funkosService.deleteFunko(anyLong())).thenThrow(new FunkosExceptions.FunkoNotFound(anyLong()));
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
    }



}
