package dev.javierhvicente.funkosb.funkos.controllers;

import dev.javierhvicente.funkosb.funkos.dto.FunkoDto;
import dev.javierhvicente.funkosb.funkos.mapper.FunkosMapper;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import dev.javierhvicente.funkosb.funkos.service.FunkosService;
import dev.javierhvicente.funkosb.utils.PageResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("${api.path:/api}/${api.version:/v1}/funkos")
public class FunkoController {
    private final FunkosService funkosService;
    private final FunkosMapper funkosMapper;
    @Autowired
    public FunkoController(FunkosService funkosService, FunkosMapper funkosMapper) {
        this.funkosService = funkosService;
        this.funkosMapper = funkosMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<Funko>> getAll(
            @RequestParam(required = false) Optional<String> categoria,
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<String> description,
            @RequestParam(required = false) Optional<Double> minPrice,
            @RequestParam(required = false) Optional<Double> maxPrice,
            @RequestParam(required = false) Optional<Boolean> isEnabled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction

            ){
        log.info("Obteniendo funkos con las siguientes condiciones: " + categoria + " " + name + " " + description + " " + minPrice + " " + maxPrice + " " + isEnabled );
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(funkosService.getAllFunkos(categoria, name, description, minPrice, maxPrice, isEnabled, pageable), sortBy, direction));
    }

    @GetMapping("{id}")
    public ResponseEntity<Funko> getFunkoById(@PathVariable Long id) {
        log.info("Obteniendo funkos por id {}", id);
        var result = funkosService.getFunkoById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("nombre/{name}")
    public ResponseEntity<Funko> getFunkoByName(@PathVariable String name) {
        log.info("Obteniendo funkos por nombre {}" , name);
        var result = funkosService.getFunkoByName(name);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Funko> createFunko(@Valid @RequestBody FunkoDto dto) {
        log.info("Creando funko");
        var result = funkosService.createFunko(funkosMapper.fromFunkoDto(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<Funko> updateFunko(@PathVariable Long id,@Valid @RequestBody FunkoDto dto) {
        log.info("Update funko on id: {}", id);
        var result = funkosService.updateFunko(id, funkosMapper.fromFunkoDto(dto));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Funko> deleteFunko(@PathVariable Long id) {
        log.info("Delete funko on id: {}", id);
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
