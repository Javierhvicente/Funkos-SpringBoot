package dev.javierhvicente.funkosb.funkos.service;

import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.funkos.exceptions.FunkosExceptions;
import dev.javierhvicente.funkosb.funkos.models.Descripcion;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import dev.javierhvicente.funkosb.funkos.repository.FunkoRepository;
import dev.javierhvicente.funkosb.funkos.service.FunkosServiceImpl;
import dev.javierhvicente.funkosb.notifications.config.WebSocketConfig;
import dev.javierhvicente.funkosb.notifications.config.WebSocketHandler;
import dev.javierhvicente.funkosb.notifications.mapper.FunkoNotificationMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FunkoServiceTest {
    private final Descripcion descripcion = new Descripcion("SoyTest");
    private final Categoria categoria = new Categoria(null, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 19.99 , LocalDateTime.now(), LocalDateTime.now());
    WebSocketHandler webSocketHandlerMock = mock(WebSocketHandler.class);
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private FunkoNotificationMapper mapper;
    @Mock
    private FunkoRepository repository;
   @InjectMocks
    private FunkosServiceImpl funkosService;

   @Test
    void getAllFunkos() {
       List<Funko> funkos = Arrays.asList(funko);
       when(repository.findAll()).thenReturn(funkos);
       List<Funko> result = funkosService.getAllFunkos();
       assertAll(
               () -> assertEquals(funkos.size(), result.size()),
               () -> assertEquals(funko.getName(), result.get(0).getName())
       );
       verify(repository, times(1)).findAll();
    }

    @Test
    void getFunkoById() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(funko));
        Funko result = funkosService.getFunkoById(1L);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(funko.getId(), result.getId()),
                () -> assertEquals(funko.getName(), result.getName())
        );
        verify(repository, times(1)).findById(1L);
    }
    @Test
    void getFunkoByIdNotFound() {
       var id = 99999L;
       when(repository.findById(id)).thenThrow(new FunkosExceptions.FunkoNotFound(id));
       var res = assertThrows(FunkosExceptions.FunkoNotFound.class, () -> funkosService.getFunkoById(id));
       verify(repository, times(1)).findById(id);
    }

    @Test
    void getFunkoByName() {
       when(repository.findByName(funko.getName())).thenReturn(funko);
       Funko res = repository.findByName(funko.getName());
       assertAll(
               () -> assertNotNull(res),
               () -> assertEquals(funko.getId(), res.getId()),
               () -> assertEquals(funko.getName(), res.getName())
       );
       verify(repository, times(1)).findByName(funko.getName());
    }

    @Test
    void getFunkoByNameNotFound() {
       var name = "Mal";
       when(repository.findByName(name)).thenThrow(new FunkosExceptions.FunkoNotFoundByName(name));
       var res = assertThrows(FunkosExceptions.FunkoNotFoundByName.class, () -> funkosService.getFunkoByName(name));
       verify(repository, times(1)).findByName(name);
    }

    @Test
    void createFunko() throws IOException {
       when(repository.save(funko)).thenReturn(funko);
       doNothing().when(webSocketHandlerMock).sendMessage(any());
       Funko res = funkosService.createFunko(funko);
       assertAll(
               () -> assertNotNull(res),
               () -> assertEquals(funko.getId(), res.getId()),
               () -> assertEquals(funko.getName(), res.getName())
       );
       verify(repository, times(1)).save(funko);
    }

   @Test
    void createFunkoErrorPrecioPorDebajoLimite(){
        Funko funkoMal = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 1.99 , LocalDateTime.now(), LocalDateTime.now());
        when(repository.save(funkoMal)).thenThrow( ConstraintViolationException.class);
        var res = assertThrows(ConstraintViolationException.class, () -> funkosService.createFunko(funkoMal));
        verify(repository, times(1)).save(funkoMal);
   }

   @Test
    void createFunkoErrorPrecioEncimaPrecioLimite(){
        Funko funkoMal = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 100.0 , LocalDateTime.now(), LocalDateTime.now());
        when(repository.save(funkoMal)).thenThrow(ConstraintViolationException.class);
        var res = assertThrows(ConstraintViolationException.class, () -> funkosService.createFunko(funkoMal));
        verify(repository, times(1)).save(funkoMal);
   }

   @Test
    void createFunkoLimitePrecioMenor(){
       Funko funkoLimite = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 10.99 , LocalDateTime.now(), LocalDateTime.now());
       when(repository.save(funkoLimite)).thenReturn(funkoLimite);
       Funko res = funkosService.createFunko(funkoLimite);
       assertAll(
               () -> assertNotNull(res),
               () -> assertEquals(funkoLimite.getId(), res.getId()),
               () -> assertEquals(funkoLimite.getName(), res.getName())
       );
       verify(repository, times(1)).save(funkoLimite);
   }

   @Test
    void createFunkoLimitePrecioMayor(){
       Funko funkoLimite = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 59.99 , LocalDateTime.now(), LocalDateTime.now());
       when(repository.save(funkoLimite)).thenReturn(funkoLimite);
       Funko res = funkosService.createFunko(funkoLimite);
       assertAll(
               () -> assertNotNull(res),
               () -> assertEquals(funkoLimite.getId(), res.getId()),
               () -> assertEquals(funkoLimite.getName(), res.getName())
       );
       verify(repository, times(1)).save(funkoLimite);
   }

   @Test
    void updateFunko() throws IOException {
       Funko funkoNew = new Funko(null, "Funko Update", descripcion, categoria,"soy.png", 19.99 , LocalDateTime.now(), LocalDateTime.now());
       
       when(repository.findById(funko.getId())).thenReturn(java.util.Optional.of(funko));
       when(repository.save(any(Funko.class))).thenReturn(funkoNew);
       doNothing().when(webSocketHandlerMock).sendMessage(any());

       Funko res = funkosService.updateFunko(funko.getId(), funkoNew);

       assertAll(
               () -> assertNotNull(res),
               () -> assertEquals(funkoNew.getId(), res.getId()),
               () -> assertEquals(funkoNew.getName(), res.getName())
       );
       verify(repository, times(1)).findById(funko.getId());
       verify(repository, times(1)).save(any(Funko.class));
   }

   @Test
    void updateFunkoNotFound(){
       Funko funkoNew = new Funko(null, "Funko Update", descripcion, categoria,"soy.png", 19.99 , LocalDateTime.now(), LocalDateTime.now());
       when(repository.findById(funko.getId())).thenThrow(new FunkosExceptions.FunkoNotFound(funko.getId()));
       var res = assertThrows(FunkosExceptions.FunkoNotFound.class, () -> funkosService.updateFunko(funko.getId(), funko));
       verify(repository, times(1)).findById(funko.getId());
       verify(repository, times(0)).save(funkoNew);
   }

   @Test
    void deleteFunko() throws IOException {
       when(repository.findById(funko.getId())).thenReturn(java.util.Optional.of(funko));
       doNothing().when(repository).deleteById(funko.getId());
       doNothing().when(webSocketHandlerMock).sendMessage(any());

       var res = funkosService.deleteFunko(funko.getId());

       assertAll(
               () -> assertEquals(funko, res)
       );
       verify(repository, times(1)).findById(funko.getId());
       verify(repository, times(1)).deleteById(funko.getId());
   }

   @Test
    void deleteFunkoNotFound(){
       when(repository.findById(funko.getId())).thenThrow(new FunkosExceptions.FunkoNotFound(funko.getId()));
       var res = assertThrows(FunkosExceptions.FunkoNotFound.class, () -> funkosService.deleteFunko(funko.getId()));
       verify(repository, times(1)).findById(funko.getId());
       verify(repository, times(0)).deleteById(funko.getId());
   }

}

