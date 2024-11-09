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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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
    private final Funko funko = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 19.99 , LocalDateTime.now(), LocalDateTime.now(), true);
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
    void getAllFunkos_ShouldReturnAllFunkos_NoParameterProvided() {
       List<Funko> funkos = Arrays.asList(funko);
       Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
       Page<Funko> expectedPage = new PageImpl<>(funkos);
       when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
       Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(), pageable);
       assertAll("findall",
               () -> assertNotNull(actualPage),
               () -> assertFalse(actualPage.isEmpty()),
               () -> assertEquals(expectedPage, actualPage)
       );
       verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_AllParameterProvided() {
        List<Funko> funkos = Arrays.asList(funko);
        Optional<String> Categoria = Optional.of("TEST");
        Optional<String> descripccion = Optional.of("SoyTest");
        Optional<String> name = Optional.of("Funko");
        Optional<Double> minPrecio = Optional.of(19.99);
        Optional<Double> maxPrecio = Optional.of(59.99);
        Optional<Boolean> enabled = Optional.of(true);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(funkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Categoria, name, descripccion, minPrecio, maxPrecio, enabled, pageable);
        assertAll("findall",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithCategoriaProvided(){
       Optional<String> Categoria = Optional.of("TEST");
         List<Funko> expectedFunkos = Arrays.asList(funko);
         Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
         Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
         when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
         Page<Funko> actualPage = funkosService.getAllFunkos(Categoria, Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(), pageable);
         assertAll("findallWithCategoria",
                 () -> assertNotNull(actualPage),
                 () -> assertFalse(actualPage.isEmpty()),
                 () -> assertEquals(expectedPage, actualPage)
         );
         verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));

    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithNameProvided(){
        Optional<String> name = Optional.of("Funko");
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), name, Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithDescripcionProvided(){
        Optional<String> descripccion = Optional.of("SoyTest");
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), descripccion, Optional.empty(),Optional.empty(), Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithMinPrecioProvided(){
        Optional<Double> precio = Optional.of(10.99);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), Optional.empty(), precio,Optional.empty(), Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithMaxPrecioProvided(){
        Optional<Double> precio = Optional.of(59.99);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),precio, Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithEnabledProvided(){
        Optional<Boolean> enabled = Optional.of(true);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(), enabled, pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithCategoriaAndNameProvided(){
        Optional<String> Categoria = Optional.of("TEST");
        Optional<String> name = Optional.of("Funko");
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Categoria, name, Optional.empty(), Optional.empty(),Optional.empty(), Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithCategoriaAndDescriptionProvided(){
        Optional<String> Categoria = Optional.of("TEST");
        Optional<String> descripccion = Optional.of("SoyTest");
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Categoria, Optional.empty(), descripccion, Optional.empty(),Optional.empty(), Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithCategoriaAndMinPriceProvided(){
        Optional<String> Categoria = Optional.of("TEST");
        Optional<Double> minPrice = Optional.of(10.99);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Categoria, Optional.empty(), Optional.empty(), minPrice,Optional.empty(), Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithCategoriaAndMaxPriceProvided(){
        Optional<String> Categoria = Optional.of("TEST");
        Optional<Double> maxPrice = Optional.of(59.99);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Categoria, Optional.empty(), Optional.empty(), Optional.empty(),maxPrice, Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithCategoriaAndEnableProvided(){
        Optional<String> Categoria = Optional.of("TEST");
        Optional<Boolean> enable = Optional.of(true);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Categoria, Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(),enable, pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithNameAndDescripccionProvided(){
        Optional<String> name = Optional.of("Funko");
        Optional<String> descripccion = Optional.of("soyTest");
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), name, descripccion, Optional.empty(),Optional.empty(),Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithNameAndMinPriceProvided(){
        Optional<String> name = Optional.of("Funko");
        Optional<Double> minPrice = Optional.of(10.99);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), name, Optional.empty(), minPrice,Optional.empty(),Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithNameAndMaxPriceProvided(){
        Optional<String> name = Optional.of("Funko");
        Optional<Double> maxPrice = Optional.of(59.99);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), name, Optional.empty(), Optional.empty(), maxPrice,Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithNameAndEnableProvided(){
        Optional<String> name = Optional.of("Funko");
        Optional<Boolean> enable = Optional.of(true);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), name, Optional.empty(), Optional.empty(), Optional.empty(),enable, pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithDescripccionAndMinPrecioProvided(){
        Optional<String> descripccion = Optional.of("SoyTest");
        Optional<Double> minPrecio = Optional.of(10.99);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), descripccion, minPrecio, Optional.empty(), Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithDescripccionAndMaxPrecioProvided(){
        Optional<String> descripccion = Optional.of("SoyTest");
        Optional<Double> maxPrecio = Optional.of(59.99);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), descripccion, Optional.empty(), maxPrecio, Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithDescripccionAndEnabledProvided(){
        Optional<String> descripccion = Optional.of("SoyTest");
        Optional<Boolean> enabled = Optional.of(true);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), descripccion, Optional.empty(), Optional.empty(), enabled, pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithMinAndMaxPrecioProvided(){
        Optional<Double> minPrecio = Optional.of(10.99);
        Optional<Double> maxPrecio = Optional.of(59.99);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), Optional.empty(), minPrecio, maxPrecio, Optional.empty(), pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithMinAndEnabledProvided(){
        Optional<Double> minPrecio = Optional.of(10.99);
        Optional<Boolean> enabled = Optional.of(true);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), Optional.empty(), minPrecio, Optional.empty(), enabled, pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getAllFunkos_ShouldReturnAllFunkos_WithMaxAndEnabledProvided(){
        Optional<Double> max = Optional.of(10.99);
        Optional<Boolean> enabled = Optional.of(true);
        List<Funko> expectedFunkos = Arrays.asList(funko);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(expectedFunkos);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Funko> actualPage = funkosService.getAllFunkos(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), max, enabled, pageable);
        assertAll("findallWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
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
        Funko funkoMal = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 1.99 , LocalDateTime.now(), LocalDateTime.now(), true);
        when(repository.save(funkoMal)).thenThrow( ConstraintViolationException.class);
        var res = assertThrows(ConstraintViolationException.class, () -> funkosService.createFunko(funkoMal));
        verify(repository, times(1)).save(funkoMal);
   }

   @Test
    void createFunkoErrorPrecioEncimaPrecioLimite(){
        Funko funkoMal = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 100.0 , LocalDateTime.now(), LocalDateTime.now(), true);
        when(repository.save(funkoMal)).thenThrow(ConstraintViolationException.class);
        var res = assertThrows(ConstraintViolationException.class, () -> funkosService.createFunko(funkoMal));
        verify(repository, times(1)).save(funkoMal);
   }

   @Test
    void createFunkoLimitePrecioMenor(){
       Funko funkoLimite = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 10.99 , LocalDateTime.now(), LocalDateTime.now(), true);
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
       Funko funkoLimite = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 59.99 , LocalDateTime.now(), LocalDateTime.now(), true);
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
       Funko funkoNew = new Funko(null, "Funko Update", descripcion, categoria,"soy.png", 19.99 , LocalDateTime.now(), LocalDateTime.now(), true);
       
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
       Funko funkoNew = new Funko(null, "Funko Update", descripcion, categoria,"soy.png", 19.99 , LocalDateTime.now(), LocalDateTime.now(), true);
       when(repository.findById(funko.getId())).thenThrow(new FunkosExceptions.FunkoNotFound(funko.getId()));
       var res = assertThrows(FunkosExceptions.FunkoNotFound.class, () -> funkosService.updateFunko(funko.getId(), funko));
       verify(repository, times(1)).findById(funko.getId());
       verify(repository, times(0)).save(funkoNew);
   }

   @Test
    void deleteFunko() throws IOException {
       when(repository.findById(funko.getId())).thenReturn(java.util.Optional.of(funko));
       when(repository.save(any(Funko.class))).thenReturn(funko);
       doNothing().when(webSocketHandlerMock).sendMessage(any());

       var res = funkosService.deleteFunko(funko.getId());

       assertAll(
               () -> assertFalse(res.getEnabled())
       );
       verify(repository, times(1)).findById(funko.getId());
   }

   @Test
    void deleteFunkoNotFound(){
       when(repository.findById(funko.getId())).thenThrow(new FunkosExceptions.FunkoNotFound(funko.getId()));
       var res = assertThrows(FunkosExceptions.FunkoNotFound.class, () -> funkosService.deleteFunko(funko.getId()));
       verify(repository, times(1)).findById(funko.getId());
       verify(repository, times(0)).deleteById(funko.getId());
   }
}

