package com.brand.practica_banco.Service;

import com.brand.practica_banco.Entity.Cuenta;
import com.brand.practica_banco.Exceptions.RecursoNoEncontradoException;
import com.brand.practica_banco.Repository.CuentaRepository;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CuentaServiceTest {

    @Mock
    private CuentaRepository repository;

    @InjectMocks
    private CuentaService service;

    @Captor
    private ArgumentCaptor<Cuenta> cuentaCaptor;

    private Cuenta validAccount;

    @BeforeEach
    void setUp() {
        validAccount = new Cuenta(1L, "Juan Perez", new BigDecimal("20000.00"));
    }

    @Nested
    public class guardarMethod{

        @Test
        public void testGuardar_successfully_noConstraintViolations() {
            //Given
            when(repository.findByNombre(any())).thenReturn(Optional.empty());
            when(repository.save(any())).thenReturn(validAccount);

            //When
            Cuenta result = service.guardar(validAccount);

            //Then
            assertThat(result).isEqualTo(validAccount);
            verify(repository).save(cuentaCaptor.capture());
            assertThat(cuentaCaptor.getValue()).isEqualTo(validAccount);
        }

        @Test
        void testGuardar_duplicateName_ConstraintViolations() {
            //Given
            Cuenta duplicateAccount = new Cuenta(2L, "Juan Perez", new BigDecimal("10000.00"));
            when(repository.findByNombre(validAccount.getNombre())).thenReturn(Optional.of(duplicateAccount));

            //When/Then
            assertThatThrownBy(() -> service.guardar(duplicateAccount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("El nombre de la cuenta ya existe. Prueba con otro.");
            verify(repository).findByNombre("Juan Perez");
            verify(repository, never()).save(any());
        }
    }

    @Nested
    public class editarMethod{
        @Test
        void testEditar_successfully_noExceptions() {
            //Given
            when(repository.existsById(validAccount.getId())).thenReturn(Boolean.TRUE);
            when(repository.save(any())).thenReturn(validAccount);

            //When
            Cuenta result = service.editar(validAccount);

            //Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(validAccount);
            verify(repository).save(cuentaCaptor.capture());
            assertThat(cuentaCaptor.getValue()).isEqualTo(validAccount);
        }

        @Test
        void testEditar_IdNotFound_RecursoNoEncontradoException() {
            //Given
            when(repository.existsById(any())).thenReturn(Boolean.FALSE);

            //When/Then
            assertThatThrownBy(() -> service.editar(validAccount))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("Cuenta con ID " + validAccount.getId() + " no encontrada.");
            verify(repository).existsById(validAccount.getId());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    public class listarMethod{
        @Test
        void testListar_withElements() {
            //Given
            Cuenta validAccount2 = new Cuenta(2L, "Juanita Perez", new BigDecimal("123.00"));
            List<Cuenta> cuentaList = List.of(validAccount, validAccount2);
            when(repository.findAll(any(Sort.class))).thenReturn(cuentaList);

            //When
            List<Cuenta> resultList = service.listar();

            //Then
            assertThat(resultList).isNotEmpty();
            assertThat(resultList).hasSize(2);
            assertThat(resultList).containsExactly(validAccount, validAccount2);
            assertThat(resultList.get(0).getId()).isEqualTo(1L);
            assertThat(resultList.get(1).getNombre()).isEqualTo("Juanita Perez");
            verify(repository).findAll(Sort.by(Sort.Direction.ASC,"id"));
        }

        @Test
        void testListar_withoutElements() {
            //Given
            when(repository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

            //When
            List<Cuenta> listCuentas = service.listar();

            //Then
            assertThat(listCuentas).isEmpty();
            verify(repository).findAll(Sort.by(Sort.Direction.ASC,"id"));
        }
    }

    @Nested
    public class buscarMethods{
        @Test
        void testBuscar_successfully_noExceptions() {
            //Given
            Long id = 1L;
            when(repository.findById(id)).thenReturn(Optional.of(validAccount));

            //When
            Cuenta found = service.buscar(id);

            //Then
            assertThat(found).isNotNull();
            assertThat(found).isEqualTo(validAccount);
            assertThat(found.getId()).isEqualTo(1L);
            assertThat(found.getNombre()).isEqualTo("Juan Perez");
            assertThat(found.getSaldo()).isEqualByComparingTo(new BigDecimal("20000.00"));
        }

        @Test
        void testBuscar_notFound_RecursoNoEncontradoException() {
            //Given
            Long id = 1L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            //When/Then
            assertThatThrownBy(() -> service.buscar(id))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("Cuenta con ID " + id + " no encontrada.");
            verify(repository).findById(id);
        }
    }

    @Nested
    public class eliminarMethods{
        @Test
        void testEliminar_successfully_noExceptions() {
            //Given
            Long id = 1L;
            when(repository.existsById(id)).thenReturn(Boolean.TRUE);

            //When
            service.eliminar(id);
            //Then
            verify(repository).deleteById(id);
        }

        @Test
        void testEliminar_elementNotFound_RecursoNoEncontradoException() {
            //Given
            Long id = 42L;
            when(repository.existsById(any())).thenReturn(Boolean.FALSE);

            //When/Then
            assertThatThrownBy(() -> service.eliminar(id))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("Cuenta con ID " + id + " no encontrada.");
            verify(repository, never()).deleteById(id);
        }
    }

    @Nested
    public class buscarPorNombreMethods{
        @Test
        void testBuscarPorNombre_successfully_noExceptions() {
            when(repository.findByNombre(validAccount.getNombre())).thenReturn(Optional.of(validAccount));

            Cuenta result = service.buscarPorNombre(validAccount.getNombre());

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNombre()).isEqualTo("Juan Perez");
            assertThat(result.getSaldo()).isEqualByComparingTo(new BigDecimal("20000.00"));
            verify(repository).findByNombre("Juan Perez");
        }

        @Test
        void testBuscarPorNombre_elementNotFound_RecursoNoEncontradoException() {
            //Given
            when(repository.findByNombre(any())).thenReturn(Optional.empty());

            //When/Then
            assertThatThrownBy(() -> service.buscarPorNombre(validAccount.getNombre()))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("Cuenta con nombre " + validAccount.getNombre() + " no encontrada.");
            verify(repository).findByNombre(validAccount.getNombre());
        }
    }
}
