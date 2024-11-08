package dev.javierhvicente.funkosb.funkos.repository;

import dev.javierhvicente.funkosb.categoria.models.Categoria;
import dev.javierhvicente.funkosb.funkos.models.Descripcion;
import dev.javierhvicente.funkosb.funkos.models.Funko;
import dev.javierhvicente.funkosb.funkos.repository.FunkoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FunkoRepositoryTest {
    private final Descripcion descripcion = new Descripcion("SoyTest");
    private final Categoria categoria = new Categoria(null, "TEST", null, LocalDateTime.now(), LocalDateTime.now(), true);
    private final Funko funko = new Funko(null, "Funko Test", descripcion, categoria,"soy.png", 19.99 , LocalDateTime.now(), LocalDateTime.now());

    @Autowired
    private FunkoRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp(){
        // Guardar la categoría y obtener su referencia persistida
        entityManager.persist(categoria);
        entityManager.flush();

        // Ahora que `categoria` es una entidad persistente, la asignamos al `funko`
        funko.setCategoria(categoria);
        entityManager.persist(funko);
        entityManager.flush();
    }


    @Test
    void findByName() {
        String name = "Funko Test";
        Funko result = repository.findByName(name);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(funko.getName(), result.getName())
        );
    }
}
