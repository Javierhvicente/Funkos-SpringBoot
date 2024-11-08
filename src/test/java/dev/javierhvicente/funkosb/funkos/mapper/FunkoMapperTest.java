package dev.javierhvicente.funkosb.funkos.mapper;

import dev.javierhvicente.funkosb.funkos.dto.FunkoDto;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FunkoMapperTest {
    private FunkosMapper mapper;
    @BeforeEach
    void setUp() {
        mapper = new FunkosMapper(); // Asegúrate de que el constructor no tenga dependencias.
    }
    @Test
    void fromFunkoDtoShouldMapCorrectly() {
        FunkoDto dto = new FunkoDto("Funko test", "TEST", "SoyTest", "soy.png", 19.99);
        Funko funko = mapper.fromFunkoDto(dto);

        assertNotNull(funko);
        assertEquals(dto.name(), funko.getName());
        assertEquals(dto.price(), funko.getPrice());


    }

}
