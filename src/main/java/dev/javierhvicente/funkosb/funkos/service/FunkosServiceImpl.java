package dev.javierhvicente.funkosb.funkos.service;

import dev.javierhvicente.funkosb.funkos.exceptions.FunkosExceptions;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import dev.javierhvicente.funkosb.funkos.repository.FunkoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.List;
@CacheConfig(cacheNames = {"Funkos"})
@Service
public class FunkosServiceImpl implements FunkosService {
    private final Logger logger = LoggerFactory.getLogger(FunkosServiceImpl.class);
    private final FunkoRepository funkoRepository;
    @Autowired
    public FunkosServiceImpl(FunkoRepository funkoRepository) {
        this.funkoRepository = funkoRepository;
    }

    @Override
    public List<Funko> getAllFunkos() {
        logger.info("Obteniendo funkos");
        return funkoRepository.findAll();
    }

    @Override
    public Funko getFunkoById(Long id) {
        logger.info("Obteniendo funko por id {}", id);
        return funkoRepository.findById(id).orElseThrow(() -> new FunkosExceptions.FunkoNotFound(id));
    }

    @Override
    public Funko getFunkoByName(String name) {
        logger.info("Obteniendo funko por nombre {}", name);
        var res =  funkoRepository.findByName(name);
        if(res == null){
            throw new FunkosExceptions.FunkoNotFoundByName(name);
        }
        return res;
    }

    @Override
    @CachePut(key = "#result.id")
    public Funko createFunko(Funko funko) {
        logger.info("Creando funko");
        return funkoRepository.save(funko);
    }

    @Override
    @CachePut(key = "#result.id")
    public Funko updateFunko(Long id, Funko funko) {
        logger.info("Actualizando persona con id {}", id);
        var res = funkoRepository.findById(id).orElseThrow( () -> new FunkosExceptions.FunkoNotFound(id));
        res.setName(funko.getName());
        res.setPrice(funko.getPrice());
        res.setCategoria(funko.getCategoria());
        res.setDescripcion(funko.getDescripcion());
        res.setImagen(funko.getImagen());
        return funkoRepository.save(res);
    }

    @Override
    @CacheEvict(key = "#result.id")
    public Funko deleteFunko(Long id) {
        logger.info("Borrando persona con id {}", id);
        Funko funko = funkoRepository.findById(id).orElseThrow(() -> new FunkosExceptions.FunkoNotFound(id));
        funkoRepository.deleteById(id);
        return funko;
    }
}
