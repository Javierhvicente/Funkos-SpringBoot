package dev.javierhvicente.funkosb.categoria.service;

import dev.javierhvicente.funkosb.categoria.exceptions.CategoriaException;
import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.categoria.repository.CategoriasRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"Categorias"})
public class CategoriasServiceImpl implements CategoriasService {
    private final Logger logger = LoggerFactory.getLogger(CategoriasServiceImpl.class);
    private final CategoriasRepository categoriasRepository;
    @Autowired
    public CategoriasServiceImpl( CategoriasRepository categoriasRepository) {
        this.categoriasRepository = categoriasRepository;
    }

    @Override
    public Page<Categoria> getAllCategorias(Optional<String> tipo, Optional<Boolean> isEnabled, Pageable pageable) {
        logger.info("Getting categorías");
        Specification<Categoria> specTipoCategoria = (root, query, criteriaBuilder) ->
                tipo.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("tipo")), "%" + m + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
        Specification<Categoria> specIsEnabled = (root, query, criteriaBuilder) ->
                isEnabled.map(m -> criteriaBuilder.equal(root.get("enabled"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
        Specification<Categoria> criterio = Specification.where(specTipoCategoria).and(specIsEnabled);
        return categoriasRepository.findAll(criterio, pageable);
    }

    @Override
    public Categoria getById(Long id) {
        logger.info("Getting categoría by id {}" , id);
        return categoriasRepository.findById(id).orElseThrow(() ->new CategoriaException.CategoriaNotFound(id));
    }

    @Override
    public Categoria getByTipo(String string) {
        logger.info("Getting categoría by tipo {}" , string);
        var result = categoriasRepository.findByTipo(string);
        if(result == null){
            throw new CategoriaException.CategoriaNotFoundByTipo(string);
        }
        return result;
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria create(Categoria categoria) {
        logger.info("Creating categoria {}" , categoria);
        return categoriasRepository.save(categoria);
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria update(Long id, Categoria categoria) {
        logger.info("Updating categoria by id {}" , id);
        var result = categoriasRepository.findById(id).orElseThrow(() -> new CategoriaException.CategoriaNotFound(id));
        result.setTipo(categoria.getTipo());
        result.setFunkos(categoria.getFunkos());
        result.setEnabled(categoria.getEnabled());
        return categoriasRepository.save(result);
    }

    @Override
    public Categoria delete(Long id) {
       logger.info("Deleting categoria by id {}" , id);
       var result = categoriasRepository.findById(id).orElseThrow(() -> new CategoriaException.CategoriaNotFound(id));
       result.setEnabled(false);
       categoriasRepository.save(result);
       return result;
    }
}
