package com.brand.practica_banco.Entity;

import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Set;

//Aquí vamos a probar las validaciones de los atributos de Cuenta

public class CuentaTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //Datos válidos
    @Test
    public void whenValidCuenta_thenNoConstraintViolations() {
        Cuenta cuenta = new Cuenta(null, "Juan Perez", new BigDecimal("5000.00"));

        Set<ConstraintViolation<Cuenta>> violations = validator.validate(cuenta);

        assertThat(violations).isEmpty();
    }

    //Agrupan los test para el atributo NOMBRE
    @Nested
    public class ValidatingConstraintsNombre {
        //Cuando el nombre está en blanco
        @Test
        public void whenNombreIsBlank_thenConstraintViolations() {
            Cuenta cuenta = new Cuenta(null, "", new BigDecimal("5000.00"));

            Set<ConstraintViolation<Cuenta>> violations = validator.validate(cuenta);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nombre") &&
                    v.getMessage().contains("El nombre no puede estar vacío."));
        }

        //Cuando el nombre tiene caracteres inválidos (números)
        @Test
        public void whenNombreHasInvalidCharacters_thenConstraintViolations() {
            Cuenta cuenta = new Cuenta(null, "Juan 123", new BigDecimal("5000.00"));

            Set<ConstraintViolation<Cuenta>> violations = validator.validate(cuenta);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nombre") &&
                    v.getMessage().contains("El nombre solo puede contener letras y espacios."));
        }

        //Cuando nombre es demasiado largo (tiene más de 100 caracteres)
        @Test
        public void whenNombreTooLong_thenConstraintViolations() {
            String longName = "A".repeat(101);
            Cuenta cuenta = new Cuenta(null, longName, new BigDecimal("5000.00"));

            Set<ConstraintViolation<Cuenta>> violations = validator.validate(cuenta);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nombre") &&
                    v.getMessage().contains("El nombre no puede tener mas de 100 caracteres."));
        }
    }

    @Nested
    public class ValidatingConstraintsSaldo {
        //Saldo nulo
        @Test
        public void whenSaldoIsNull_thenConstraintViolations() {
            Cuenta cuenta = new Cuenta(null, "Juan Perez", null);

            Set<ConstraintViolation<Cuenta>> violations = validator.validate(cuenta);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("saldo") &&
                    v.getMessage().contains("El saldo no puede ser nulo."));
        }

        //Saldo con valor negativo
        @Test
        public void whenSaldoIsNegative_thenConstraintViolations() {
            Cuenta cuenta = new Cuenta(null, "Juan Perez", new BigDecimal("-50000.00"));

            Set<ConstraintViolation<Cuenta>> violations = validator.validate(cuenta);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("saldo") &&
                    v.getMessage().contains("El saldo no puede ser negativo."));
        }

        //Saldo que excede el permitido ($10,000,000.00)
        @Test
        void whenSaldoExceedsMax_thenConstraintViolations() {
            Cuenta cuenta = new Cuenta(null, "Juan Perez", new BigDecimal("15000000.00"));

            Set<ConstraintViolation<Cuenta>> violations = validator.validate(cuenta);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("saldo") &&
                    v.getMessage().equals("El saldo no puede exceder $10,000,000.00"));
        }

        //Saldo con mas de 2 decimales
        @Test
        void whenSaldoHasMoreThanTwoDecimals_thenConstraintViolations() {
            Cuenta cuenta = new Cuenta(null, "Juan Perez", new BigDecimal("10000.123"));

            Set<ConstraintViolation<Cuenta>> violations = validator.validate(cuenta);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("saldo") &&
                    v.getMessage().contains("El saldo debe tener máximo 2 decimales."));
        }

        // Nota: Los campos de auditoría (fechaCreacion y fechaActualizacion) se asignan
        // automáticamente al persistir la entidad a través de JPA, por lo que en pruebas
        // unitarias de Bean Validation, normalmente se esperan como nulos.

        // Validación del builder: se comprueba que se asignen correctamente los campos y que los campos de auditoría sean nulos (sin persistir)
        @Test
        void whenCuentaBuilt_thenFieldsAreSetCorrectly() {
            Cuenta cuenta = new Cuenta(10L, "Test Builder", new BigDecimal("50000.00"));

            assertThat(cuenta.getId()).isEqualTo(10L);
            assertThat(cuenta.getNombre()).isEqualTo("Test Builder");
            assertThat(cuenta.getSaldo()).isEqualByComparingTo(new BigDecimal("50000.00"));
            // Al usar el builder sin persistir, los campos de auditoría permanecen nulos.
            assertThat(cuenta.getFechaCreacion()).isNull();
            assertThat(cuenta.getFechaActualizacion()).isNull();

        }
    }
}
