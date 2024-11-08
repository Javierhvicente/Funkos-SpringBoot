package dev.javierhvicente.funkosb.funkos.controllers;

import dev.javierhvicente.funkosb.funkos.dto.FunkoDto;
import dev.javierhvicente.funkosb.funkos.mapper.FunkosMapper;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import dev.javierhvicente.funkosb.funkos.service.FunkosService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path:/api}/${api.version:/v1}/funkos")
public class FunkoController {
    private final Logger logger = LoggerFactory.getLogger(FunkoController.class);
    private final FunkosService funkosService;
    private final FunkosMapper funkosMapper;
    @Autowired
    public FunkoController(FunkosService funkosService, FunkosMapper funkosMapper) {
        this.funkosService = funkosService;
        this.funkosMapper = funkosMapper;
    }

    @GetMapping
    public ResponseEntity<List<Funko>> getAll() {
        logger.info("Obteniendo funkos");
        return ResponseEntity.ok((funkosService.getAllFunkos()));
    }

    @GetMapping("{id}")
    public ResponseEntity<Funko> getFunkoById(@PathVariable Long id) {
        logger.info("Obteniendo funkos por id {}", id);
        var result = funkosService.getFunkoById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("nombre/{name}")
    public ResponseEntity<Funko> getFunkoByName(@PathVariable String name) {
        logger.info("Obteniendo funkos por nombre {}" , name);
        var result = funkosService.getFunkoByName(name);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Funko> createFunko(@Valid @RequestBody FunkoDto dto) {
        logger.info("Creando funko");
        var result = funkosService.createFunko(funkosMapper.fromFunkoDto(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<Funko> updateFunko(@PathVariable Long id,@Valid @RequestBody FunkoDto dto) {
        logger.info("Update funko on id: {}", id);
        var result = funkosService.updateFunko(id, funkosMapper.fromFunkoDto(dto));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Funko> deleteFunko(@PathVariable Long id) {
        logger.info("Delete funko on id: {}", id);
        var result = funkosService.deleteFunko(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
