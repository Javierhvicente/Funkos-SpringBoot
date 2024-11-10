package dev.javierhvicente.funkosb.categoria.service;

import dev.javierhvicente.funkosb.categoria.exceptions.CategoriaException;
import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.categoria.repository.CategoriasRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoriaServiceImplTest {
    private final Categoria categoria = new Categoria(1L, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true);
    @Mock
    private CategoriasRepository categoriaRepository;
    @InjectMocks
    private CategoriasServiceImpl categoriaService;

    @Test
    void getCategorias_withoutParams(){
        List<Categoria> categorias = Arrays.asList(categoria);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(categorias);
        when(categoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Categoria> actualPage = categoriaService.getAllCategorias(Optional.empty(), Optional.empty(), pageable);
        assertAll("getAllCategorias",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(categoriaRepository,times(1) ).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getCategorias_withTipoProvided(){
        Optional<String> tipo = Optional.of("TEST");
        List<Categoria> expectedCategorias = Arrays.asList(categoria);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(expectedCategorias);
        when(categoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Categoria> actualPage = categoriaService.getAllCategorias(tipo, Optional.empty(), pageable);
        assertAll("getAllCategorias",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(categoriaRepository,times(1) ).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getCategorias_withEnabledProvided(){
        Optional<Boolean> enabled = Optional.of(true);
        List<Categoria> expectedCategorias = Arrays.asList(categoria);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(expectedCategorias);
        when(categoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Categoria> actualPage = categoriaService.getAllCategorias( Optional.empty(), enabled, pageable);
        assertAll("getAllCategorias",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(categoriaRepository,times(1) ).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getCategorias_withAllParamsProvided(){
        Optional<String> tipo = Optional.of("TEST");
        Optional<Boolean> enabled = Optional.of(true);
        List<Categoria> expectedCategorias = Arrays.asList(categoria);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(expectedCategorias);
        when(categoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        Page<Categoria> actualPage = categoriaService.getAllCategorias(tipo, enabled, pageable);
        assertAll("getAllCategorias",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
        verify(categoriaRepository,times(1) ).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void getCategoriaById(){
        when(categoriaRepository.findById(1L)).thenReturn(java.util.Optional.of(categoria));
        var result = categoriaService.getById(1L);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(categoria.getId(), result.getId()),
                () -> assertEquals(categoria.getTipo(), result.getTipo())
        );
        verify(categoriaRepository, times(1) ).findById(1L);
    }

    @Test
    void getCategoriaByIdNotFound(){
        var id = 99999L;
        when(categoriaRepository.findById(id)).thenThrow(new CategoriaException.CategoriaNotFound(id));
        var result = assertThrows(CategoriaException.CategoriaNotFound.class, () -> categoriaService.getById(id));
        verify(categoriaRepository, times(1) ).findById(id);
    }

    @Test
    void save(){
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        var result = categoriaService.create(new Categoria(null, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true));
        assertAll("save",
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getId())
        );
        verify(categoriaRepository, times(1) ).save(any(Categoria.class));
    }

    @Test
    void update(){
        when(categoriaRepository.findById(1L)).thenReturn(java.util.Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        var result = categoriaService.update(1L, new Categoria(1L, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true));
        assertAll("update",
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("TEST", result.getTipo())
        );
        verify(categoriaRepository, times(1) ).findById(1L);
        verify(categoriaRepository, times(1) ).save(any(Categoria.class));
    }

    @Test
    void updateNotFound(){
        var id = 9999L;
        when(categoriaRepository.findById(id)).thenThrow(new CategoriaException.CategoriaNotFound(id));
        var result = assertThrows(CategoriaException.CategoriaNotFound.class, () -> categoriaService.getById(id));
        verify(categoriaRepository, times(1) ).findById(id);
        verify(categoriaRepository, times(0)).save(categoria);
    }

    @Test
    void delete(){
        when(categoriaRepository.findById(1L)).thenReturn(java.util.Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(new Categoria(1L, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), false));
        var result = categoriaService.delete(1L);
        assertAll("delete",
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getId()),
                () -> assertFalse(result.getEnabled())
        );
        verify(categoriaRepository, times(1) ).findById(1L);
        verify(categoriaRepository, times(1) ).save(any(Categoria.class));
    }

    @Test
    void deleteNotFound(){
        var id = 9999L;
        when(categoriaRepository.findById(id)).thenThrow(new CategoriaException.CategoriaNotFound(id));
        var result = assertThrows(CategoriaException.CategoriaNotFound.class, () -> categoriaService.delete(id));
        verify(categoriaRepository, times(1) ).findById(id);
        verify(categoriaRepository, times(0)).save(categoria);
    }




}
