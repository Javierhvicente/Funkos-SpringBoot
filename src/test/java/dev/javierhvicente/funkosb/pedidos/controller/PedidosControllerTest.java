package dev.javierhvicente.funkosb.pedidos.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.javierhvicente.funkosb.pedidos.exceptions.PedidoNotFound;
import dev.javierhvicente.funkosb.pedidos.exceptions.PedidoNotItems;
import dev.javierhvicente.funkosb.pedidos.exceptions.ProductoBadPrice;
import dev.javierhvicente.funkosb.pedidos.exceptions.ProductoNotFound;
import dev.javierhvicente.funkosb.pedidos.models.Cliente;
import dev.javierhvicente.funkosb.pedidos.models.Direccion;
import dev.javierhvicente.funkosb.pedidos.models.LineaPedido;
import dev.javierhvicente.funkosb.pedidos.models.Pedido;
import dev.javierhvicente.funkosb.pedidos.service.PedidosService;
import dev.javierhvicente.funkosb.utils.PageResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
public class PedidosControllerTest{
    private final String myEndPoint = "/funkos/v1/pedidos";
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();
    private final Pedido pedido1 = Pedido.builder()
            .id(new ObjectId("5f9f1a3b9d6b6d2e3c1d6f1a"))
            .idUsuario(1L)
            .cliente(
                    new Cliente("test", "test@test.com", "1234567890",
                            new Direccion("Calle", "1", "Ciudad", "Provincia", "Pais", "28054")
                    )
            )
            .lineasPedido(List.of(LineaPedido.builder()
                    .idProducto(1L)
                    .cantidad(2)
                    .precioProducto(10.0)
                    .build()))
            .build();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private PedidosService pedidosService;
    @Autowired
    private JacksonTester<Pedido> jsonPedido;
    @Autowired
    public PedidosControllerTest(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
        mapper.registerModule(new JavaTimeModule()); // Necesario para que funcione LocalDateTime
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllPedidos() throws Exception {
        var pedidosList = List.of(pedido1);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(pedidosList);

        when(pedidosService.findAll(pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Pedido> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(pedidosService, times(1)).findAll(any(Pageable.class));
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void getPedidoById() throws Exception {
        var myLocalEndpoint = myEndPoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";
        when(pedidosService.findById(any(ObjectId.class))).thenReturn(pedido1);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Pedido res = mapper.readValue(response.getContentAsString(), Pedido.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(pedido1, res)
        );

        verify(pedidosService, times(1)).findById(any(ObjectId.class));
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void getPedidoByIdNoFound() throws Exception {
        var myLocalEndpoint = myEndPoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        when(pedidosService.findById(any(ObjectId.class)))
                .thenThrow(new PedidoNotFound("5f9f1a3b9d6b6d2e3c1d6f1a"));


        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(pedidosService, times(1)).findById(any(ObjectId.class));
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void getPedidosByUsuarioId() throws Exception {
        var myLocalEndpoint = myEndPoint + "/usuario/1";
        var pedidosList = List.of(pedido1);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(pedidosList);

        when(pedidosService.findByIdUsuario(anyLong(), any(Pageable.class))).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Pedido> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(pedidosService, times(1)).findByIdUsuario(anyLong(), any(Pageable.class));
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void createPedido() throws Exception {
        when(pedidosService.save(any(Pedido.class))).thenReturn(pedido1);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPedido.write(pedido1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Pedido res = mapper.readValue(response.getContentAsString(), Pedido.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(pedido1, res)
        );

        verify(pedidosService, times(1)).save(any(Pedido.class));
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void createPedidoNoItemsBadRequest() throws Exception {
        when(pedidosService.save(any(Pedido.class))).thenThrow(new PedidoNotItems("5f9f1a3b9d6b6d2e3c1d6f1a"));

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPedido.write(pedido1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        verify(pedidosService).save(any(Pedido.class));
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void createPedidoProductoBadPriceBadRequest() throws Exception {
        when(pedidosService.save(any(Pedido.class))).thenThrow(new ProductoBadPrice(1L));

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPedido.write(pedido1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
        verify(pedidosService).save(any(Pedido.class));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getPedidosProductoNotFoundBadRequest() throws Exception {
        when(pedidosService.save(any(Pedido.class))).thenThrow(new ProductoNotFound(1L));

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPedido.write(pedido1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
        verify(pedidosService).save(any(Pedido.class));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProduct() throws Exception {
        var myLocalEndpoint = myEndPoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";
        when(pedidosService.update(any(ObjectId.class), any(Pedido.class))).thenReturn(pedido1);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPedido.write(pedido1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Pedido res = mapper.readValue(response.getContentAsString(), Pedido.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(pedido1, res)
        );

        verify(pedidosService, times(1)).update(any(ObjectId.class), any(Pedido.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updatePedidoNoFound() throws Exception {
        var myLocalEndpoint = myEndPoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        when(pedidosService.update(any(ObjectId.class), any(Pedido.class)))
                .thenThrow(new PedidoNotFound("5f9f1a3b9d6b6d2e3c1d6f1a"));

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPedido.write(pedido1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(pedidosService, times(1)).update(any(ObjectId.class), any(Pedido.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updatePedidoNoItemsBadRequest() throws Exception {
        var myLocalEndpoint = myEndPoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        when(pedidosService.update(any(ObjectId.class), any(Pedido.class)))
                .thenThrow(new PedidoNotItems("5f9f1a3b9d6b6d2e3c1d6f1a"));

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPedido.write(pedido1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
        verify(pedidosService, times(1)).update(any(ObjectId.class), any(Pedido.class));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updatePedidoProductoBadPriceBadRequest() throws Exception {
        var myLocalEndpoint = myEndPoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        when(pedidosService.update(any(ObjectId.class), any(Pedido.class)))
                .thenThrow(new ProductoBadPrice(1L));

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPedido.write(pedido1).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
        verify(pedidosService, times(1)).update(any(ObjectId.class), any(Pedido.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletePedido() throws Exception {
        var myLocalEndpoint = myEndPoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";
        doNothing().when(pedidosService).delete(any(ObjectId.class));

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(204, response.getStatus())
        );
        verify(pedidosService, times(1)).delete(any(ObjectId.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletePedidoNoFound() throws Exception {
        var myLocalEndpoint = myEndPoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        doThrow(new PedidoNotFound("5f9f1a3b9d6b6d2e3c1d6f1a")).when(pedidosService).delete(any(ObjectId.class));

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(pedidosService, times(1)).delete(any(ObjectId.class));
    }
}
