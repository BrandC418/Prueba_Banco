package com.brand.practica_banco.Repository;

import com.brand.practica_banco.Entity.Cuenta;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@DataJpaTest
public class CuentaRepositoryTest {

    @Autowired
    private CuentaRepository repository;

    //Vamos a probar los metodos basicos de CRUD
    @Nested
    public class MethodsCRUD{

        @Test
        public void testSave(){
            Cuenta cuenta = new Cuenta(null, "Juan Perez", new BigDecimal("50000.00"));

            Cuenta saved = repository.save(cuenta);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo(cuenta.getId());
            assertThat(saved.getNombre()).isEqualTo("Juan Perez");
            assertThat(saved.getSaldo()).isEqualByComparingTo(new BigDecimal("50000.00"));
//            assertThat(saved.getFechaCreacion())
//                    .isCloseTo(LocalDateTime.now(), within(500, ChronoUnit.MILLIS));
//            assertThat(saved.getFechaActualizacion()).isCloseTo(LocalDateTime.now(), within(500, ChronoUnit.MILLIS));
        }

        @Test
        void testRead() {
            Cuenta cuenta = new Cuenta(null, "Juan Perez", new BigDecimal("500.00"));
            Cuenta saved = repository.save(cuenta);

            Optional<Cuenta> found = repository.findById(saved.getId());

            assertThat(found).isNotEmpty();
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(cuenta.getId());
            assertThat(found.get().getNombre()).isEqualTo("Juan Perez");
            assertThat(found.get().getSaldo()).isEqualByComparingTo(new BigDecimal("500.00"));
        }

        @Test
        void testUpdate() {
            Cuenta cuentaOriginal = new Cuenta(null, "Juan Perez", new BigDecimal("5000.00"));
            Cuenta savedCuenta = repository.save(cuentaOriginal);

            savedCuenta.setNombre("Ernesto");
            savedCuenta.setSaldo(new BigDecimal("123123.00"));
            Cuenta updatedCuenta = repository.save(savedCuenta);

            assertThat(updatedCuenta).isNotNull();
            assertThat(updatedCuenta.getId()).isEqualTo(savedCuenta.getId());
            assertThat(updatedCuenta.getNombre()).isEqualTo(savedCuenta.getNombre());
            assertThat(updatedCuenta.getSaldo()).isEqualByComparingTo(savedCuenta.getSaldo());
            // Verificar fechas (si aplica)
//            assertThat(updatedCuenta.getFechaCreacion()).isEqualTo(savedCuenta.getFechaCreacion()); // Fecha de creación no cambia
//            assertThat(updatedCuenta.getFechaActualizacion()).isAfterOrEqualTo(savedCuenta.getFechaActualizacion()); // Fecha de actualización cambia
        }

        @Test
        void testDelete() {
            Cuenta cuenta = new Cuenta(null, "Juan Perez", new BigDecimal("2000.00"));
            Cuenta saved = repository.save(cuenta);

            repository.deleteById(cuenta.getId());
            Optional<Cuenta> found = repository.findById(cuenta.getId());

            assertThat(found).isEmpty();
        }
    }

    //Probando metodo personalizado (findByNombre)
    @Test
    void testFindByNombre() {
        Cuenta cuenta = new Cuenta(null, "Juan Perez", new BigDecimal("2000.00"));
        Cuenta saved = repository.save(cuenta);

        Optional<Cuenta> found = repository.findByNombre(saved.getNombre());

        assertThat(found).isNotEmpty();
        assertThat(found.get().getNombre()).isEqualTo("Juan Perez");
        assertThat(found.get().getSaldo()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    //Test de Nombre duplicado
    @Test
    void testUniqueConstraint() {
        Cuenta cuenta1 = new Cuenta(null, "Duplicado", new BigDecimal("2000.00"));
        Cuenta cuenta2 = new Cuenta(null, "Duplicado", new BigDecimal("1000.00"));
        repository.save(cuenta1);

        assertThatThrownBy(() -> repository.saveAndFlush(cuenta2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
