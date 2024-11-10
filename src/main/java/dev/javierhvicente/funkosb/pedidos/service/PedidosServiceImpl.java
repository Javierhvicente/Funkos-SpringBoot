package dev.javierhvicente.funkosb.pedidos.service;

import dev.javierhvicente.funkosb.funkos.service.FunkosService;
import dev.javierhvicente.funkosb.pedidos.exceptions.*;
import dev.javierhvicente.funkosb.pedidos.models.LineaPedido;
import dev.javierhvicente.funkosb.pedidos.models.Pedido;
import dev.javierhvicente.funkosb.pedidos.repository.PedidosRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PedidosServiceImpl implements PedidosService {
    private final PedidosRepository pedidosRepository;
    private final FunkosService funkosService;

    public PedidosServiceImpl(PedidosRepository pedidosRepository, FunkosService funkosService) {
        this.pedidosRepository = pedidosRepository;
        this.funkosService = funkosService;
    }

    @Override
    public Page<Pedido> findAll(Pageable pageable) {
        log.info("Obteniendo todos los pedidos paginados y ordenados con {}", pageable);
        return pedidosRepository.findAll(pageable);
    }

    @Override
    @Cacheable("pedidos")
    public Pedido findById(ObjectId idPedido) {
        log.info("Obteniendo pedido con id: " + idPedido);
        return pedidosRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toHexString()));
    }

    @Override
    public Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable) {
        log.info("Obteniendo pedidos del usuario con id: " + idUsuario);
        return pedidosRepository.findByIdUsuario(idUsuario, pageable);
    }

    @Override
    @Transactional
    @CachePut("pedidos")
    public Pedido save(Pedido pedido) {
        log.info("Guardando pedido: {}", pedido);
        checkPedido(pedido);
        var pedidoToSave = reserveStock(pedido);
        pedidoToSave.setCreatedAt(LocalDateTime.now());
        pedidoToSave.setUpdatedAt(LocalDateTime.now());
        return pedidosRepository.save(pedidoToSave);

    }

    public Pedido reserveStock(Pedido pedido) {
        log.info("Reservando stock para el pedido: {}", pedido);

        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }

        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funko = funkosService.getFunkoById(lineaPedido.getIdProducto());
            funko.setStock(funko.getStock() - lineaPedido.getCantidad());
            funkosService.updateFunko(funko.getId(), funko);
            lineaPedido.setTotal(lineaPedido.getCantidad() * funko.getPrice());
        });
        var total = pedido.getLineasPedido().stream()
                .map(lineaPedido -> lineaPedido.getCantidad() * lineaPedido.getPrecioProducto())
                .reduce(0.0, Double::sum);
        var totalItems = pedido.getLineasPedido().stream()
                .map(LineaPedido::getCantidad)
                .reduce(0, Integer::sum);
        pedido.setTotal(total);
        pedido.setTotalItems(totalItems);
        return pedido;
    }

    @Override
    @Transactional
    @CacheEvict("pedidos")
    public void delete(ObjectId idPedido) {
        log.info("Borrando pedido con id: " + idPedido);
        var pedidoDelete = this.findById(idPedido);
        returnStockPedido(pedidoDelete);
        pedidosRepository.delete(pedidoDelete);
    }

    public Pedido returnStockPedido(Pedido pedido) {
        log.info("Retornando stock del pedido: {}", pedido);
        if (pedido.getLineasPedido() != null) {
            pedido.getLineasPedido().forEach(lineaPedido -> {
                var funko = funkosService.getFunkoById(lineaPedido.getIdProducto());
                funko.setStock(funko.getStock() + lineaPedido.getCantidad());
                funkosService.updateFunko(funko.getId(), funko);
            });
        }
        return pedido;
    }

    @Override
    @Transactional
    @CachePut("pedidos")
    public Pedido update(ObjectId idPedido, Pedido pedido) {
        log.info("Actualizando pedido con id: " + idPedido);
        var pedidoToUpdate = this.findById(idPedido);
        returnStockPedido(pedido);
        checkPedido(pedido);
        var pedidoToSave = reserveStock(pedido);
        pedidoToSave.setUpdatedAt(LocalDateTime.now());
        return pedidosRepository.save(pedidoToSave);
    }

    public void checkPedido(Pedido pedido) {
        log.info("Comprobando pedido: {}", pedido);
        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }
        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funko = funkosService.getFunkoById(lineaPedido.getIdProducto());
            // Si existe, comprobamos si hay stock
            if (funko.getStock() < lineaPedido.getCantidad()) {
                throw new ProductoNotStock(lineaPedido.getIdProducto());
            }
            // Podemos comprobar más cosas, como si el precio es el mismo, etc...
            if (!funko.getPrice().equals(lineaPedido.getPrecioProducto())) {
                throw new ProductoBadPrice(lineaPedido.getIdProducto());
            }
        });
    }
}
