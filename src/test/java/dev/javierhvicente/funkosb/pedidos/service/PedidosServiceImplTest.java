package dev.javierhvicente.funkosb.pedidos.service;

import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.funkos.models.Descripcion;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import dev.javierhvicente.funkosb.funkos.service.FunkosService;
import dev.javierhvicente.funkosb.pedidos.exceptions.PedidoNotFound;
import dev.javierhvicente.funkosb.pedidos.exceptions.PedidoNotItems;
import dev.javierhvicente.funkosb.pedidos.exceptions.ProductoBadPrice;
import dev.javierhvicente.funkosb.pedidos.exceptions.ProductoNotStock;
import dev.javierhvicente.funkosb.pedidos.models.LineaPedido;
import dev.javierhvicente.funkosb.pedidos.models.Pedido;
import dev.javierhvicente.funkosb.pedidos.repository.PedidosRepository;
import dev.javierhvicente.funkosb.pedidos.service.PedidosServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidosServiceImplTest {
    @Mock
    private PedidosRepository pedidosRepository;
    @Mock
    private FunkosService funkosService;
    @InjectMocks
    private PedidosServiceImpl pedidosService;
    private final Descripcion descripcion = new Descripcion("SoyTest");
    private final Categoria categoria = new Categoria(null, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko = new Funko(1L, "Funko Test", descripcion, categoria,"soy.png", 19.99 ,10, LocalDateTime.now(), LocalDateTime.now(), true);

    @Test
    public void findAll() {
        List<Pedido> pedidos = List.of(new Pedido(), new Pedido());
        Page<Pedido> expectedPage = new PageImpl<>(pedidos);
        Pageable pageable = PageRequest.of(0, 10);
        when(pedidosRepository.findAll(pageable)).thenReturn(expectedPage);
        Page<Pedido> result = pedidosService.findAll(pageable);
        assertAll(
                () -> assertEquals(expectedPage, result),
                () -> assertEquals(expectedPage.getContent(), result.getContent()),
                () -> assertEquals(expectedPage.getTotalElements(), result.getTotalElements())
        );
        verify(pedidosRepository, times(1)).findAll(pageable);
    }

    @Test
    public void findById() {
        ObjectId idPedido = new ObjectId();
        Pedido expectedPedido = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(java.util.Optional.of(expectedPedido));
        Pedido result = pedidosService.findById(idPedido);
        assertEquals(expectedPedido, result);
        verify(pedidosRepository, times(1)).findById(idPedido);
    }
    @Test
    public void findByIdNotFound(){
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(java.util.Optional.empty());
        assertThrows(PedidoNotFound.class, () -> pedidosService.findById(idPedido));
        verify(pedidosRepository, times(1)).findById(idPedido);
    }
    @Test
    public void findByIdUsuario() {
        List<Pedido> pedidos = List.of(new Pedido(), new Pedido());
        Page<Pedido> expectedPage = new PageImpl<>(pedidos);
        Pageable pageable = PageRequest.of(0, 10);
        Long idUsuario = 1L;
        when(pedidosRepository.findByIdUsuario(idUsuario, pageable)).thenReturn(expectedPage);
        Page<Pedido> result = pedidosService.findByIdUsuario(idUsuario, pageable);
        assertAll(
                () -> assertEquals(expectedPage, result),
                () -> assertEquals(expectedPage.getContent(), result.getContent()),
                () -> assertEquals(expectedPage.getTotalElements(), result.getTotalElements())
        );
        verify(pedidosRepository, times(1)).findByIdUsuario(idUsuario, pageable);
    }
    @Test
    void Save() {
        // Arrange
        Pedido pedido = new Pedido();
        LineaPedido lineaPedido = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(19.99)
                .build();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToSave = new Pedido();
        pedidoToSave.setLineasPedido(List.of(lineaPedido));

        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoToSave); // Utiliza any(Pedido.class) para cualquier instancia de Pedido
        when(funkosService.getFunkoById(anyLong())).thenReturn(funko);

        // Act
        Pedido resultPedido = pedidosService.save(pedido);

        // Assert
        assertAll(
                () -> assertEquals(pedidoToSave, resultPedido),
                () -> assertEquals(pedidoToSave.getLineasPedido(), resultPedido.getLineasPedido()),
                () -> assertEquals(pedidoToSave.getLineasPedido().size(), resultPedido.getLineasPedido().size())
        );

        // Verify
        verify(pedidosRepository).save(any(Pedido.class));
        verify(funkosService, times(2)).getFunkoById(anyLong());
    }
    @Test
    void saveNoFunkos(){
        Pedido pedido = new Pedido();
        assertThrows(PedidoNotItems.class, () -> pedidosService.save(pedido));
        // Verify
        verify(pedidosRepository, never()).save(any(Pedido.class));
        verify(funkosService, never()).getFunkoById(anyLong());
    }
    @Test
    void delete() {
        ObjectId idPedido = new ObjectId();
        Pedido pedidoToDelito = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToDelito));
        pedidosService.delete(idPedido);
        verify(pedidosRepository, times(1)).delete(pedidoToDelito);
        verify(pedidosRepository, never()).save(any(Pedido.class));
    }
    @Test
    void deleteNotFound() {
        ObjectId idPedido = new ObjectId();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFound.class, () -> pedidosService.delete(idPedido));
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository, never()).deleteById(idPedido);
    }
    @Test
    void update(){
        LineaPedido lineaPedido = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(19.99)
                .build();
        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        pedido.setLineasPedido(List.of(lineaPedido));
        Pedido pedidoToupdate = new Pedido();
        pedidoToupdate.setLineasPedido(List.of(lineaPedido));
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedidoToupdate));
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoToupdate);
        when(funkosService.getFunkoById(anyLong())).thenReturn(funko);
        Pedido result = pedidosService.update(idPedido, pedido);
        assertAll(
                () -> assertEquals(pedidoToupdate, result),
                () -> assertEquals(pedidoToupdate.getLineasPedido(), result.getLineasPedido()),
                () -> assertEquals(pedidoToupdate.getLineasPedido().size(), result.getLineasPedido().size())
        );
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository).save(any(Pedido.class));
        verify(funkosService, times(3)).getFunkoById(anyLong());
    }
    @Test
    void updateNotFound(){
        ObjectId idPedido = new ObjectId();
        Pedido pedido = new Pedido();
        when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFound.class, () -> pedidosService.update(idPedido, pedido));
        verify(pedidosRepository).findById(idPedido);
        verify(pedidosRepository, never()).save(any(Pedido.class));
        verify(funkosService, never()).getFunkoById(anyLong());
    }
    @Test
    void reserveStock(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(19.99)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getFunkoById(anyLong())).thenReturn(funko);
        Pedido result = pedidosService.reserveStock(pedido);
        assertAll(
                () -> assertEquals(pedido, result),
                () -> assertEquals(pedido.getLineasPedido(), result.getLineasPedido()),
                () -> assertEquals(pedido.getLineasPedido().size(), result.getLineasPedido().size())
        );
        verify(funkosService, times(1)).getFunkoById(anyLong());
        verify(funkosService, times(1)).updateFunko(anyLong(), any(Funko.class));
    }
    @Test
    void reserveStockNoLineas(){
        Pedido pedido = new Pedido();
        assertThrows(PedidoNotItems.class, () -> pedidosService.reserveStock(pedido));
        verify(funkosService, never()).getFunkoById(anyLong());
        verify(funkosService, never()).updateFunko(anyLong(), any(Funko.class));
    }
    @Test
    void returnStockPedido(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(19.99)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getFunkoById(anyLong())).thenReturn(funko);
        when(funkosService.updateFunko(anyLong(), any(Funko.class))).thenReturn(funko);
        Pedido result = pedidosService.returnStockPedido(pedido);
        assertEquals(12, funko.getStock());
        assertEquals(pedido, result);
        verify(funkosService, times(1)).getFunkoById(anyLong());
        verify(funkosService, times(1)).updateFunko(anyLong(), any(Funko.class));
    }
    @Test
    void checkPedido(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(19.99)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getFunkoById(anyLong())).thenReturn(funko);
        assertDoesNotThrow(() -> pedidosService.checkPedido(pedido));
        verify(funkosService, times(1)).getFunkoById(anyLong());
    }
    @Test
    void checkPedidoNoLineas(){
        Pedido pedido = new Pedido();
        assertThrows(PedidoNotItems.class, () -> pedidosService.checkPedido(pedido));
        verify(funkosService, never()).getFunkoById(anyLong());
    }
    @Test
    void checkPedidoStockUnderCantidad(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(11)
                .precioProducto(19.99)
                .build();
        lineaPedido1.setIdProducto(1L);
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getFunkoById(anyLong())).thenReturn(funko);
        assertThrows(ProductoNotStock.class, () -> pedidosService.checkPedido(pedido));
        verify(funkosService, times(1)).getFunkoById(anyLong());
    }
    @Test
    void checkPedidoPrecioNotMatchProduct(){
        Pedido pedido = new Pedido();
        List<LineaPedido> lineasPedido = new ArrayList<>();
        LineaPedido lineaPedido1 = LineaPedido.builder()
                .idProducto(1L)
                .cantidad(2)
                .precioProducto(20.99)
                .build();
        lineasPedido.add(lineaPedido1);
        pedido.setLineasPedido(lineasPedido);
        when(funkosService.getFunkoById(anyLong())).thenReturn(funko);
        assertThrows(ProductoBadPrice.class, () -> pedidosService.checkPedido(pedido));
        verify(funkosService, times(1)).getFunkoById(anyLong());
    }

}
