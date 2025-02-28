package com.brand.practica_banco.Controller;

import com.brand.practica_banco.Entity.Cuenta;
//import com.brand.practica_banco.Entity.CuentaDto;
import com.brand.practica_banco.Entity.CuentaDto;
import com.brand.practica_banco.Exceptions.RecursoNoEncontradoException;
import com.brand.practica_banco.Service.CuentaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class) // Solo carga el controlador y sus dependencias mockeadas
public class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Usa @MockBean en lugar de @MockitoBean
    private CuentaService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    public class listarMethods {

        @Test
        void testListar_withElements_noExceptions() throws Exception {
            //Given
            Cuenta cuenta1 = new Cuenta(1L, "Cuenta uno", new BigDecimal("10000.00"));
            Cuenta cuenta2 = new Cuenta(2L, "Cuenta dos", new BigDecimal("20000.00"));
            when(service.listar()).thenReturn(Arrays.asList(cuenta1, cuenta2));

            // Realiza la solicitud GET y verifica la respuesta
            mockMvc.perform(get("/api/banco/cuenta"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre").value("Cuenta uno"))
                    .andExpect(jsonPath("$[0].saldo").value(10000.00))
                    .andExpect(jsonPath("$[1].nombre").value("Cuenta dos"))
                    .andExpect(jsonPath("$[1].saldo").value(20000.00));

        }

        @Test
        void testListar_EmptyList_noContent() throws Exception{
            when(service.listar()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/banco/cuenta"))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    public class guardarMethods{
        @Test
        void testGuardar_successfully_noExceptions() throws Exception {
            CuentaDto cuentaDto = new CuentaDto(null, "Cuenta Guardar", new BigDecimal("20000.00"));
            Cuenta cuentaGuardada = new Cuenta(1L, "Cuenta Guardar", new BigDecimal("20000.00"));
            when(service.guardar(any(Cuenta.class))).thenReturn(cuentaGuardada);

            mockMvc.perform(post("/api/banco/cuenta")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cuentaDto)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/banco/cuenta/1"))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombre").value("Cuenta Guardar"));
        }

        @Test
        void testGuardar_invalidAccount_thenException() throws Exception {
            CuentaDto cuentaDto = new CuentaDto(null, "", new BigDecimal("20000.00"));

            mockMvc.perform(post("/api/banco/cuenta")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cuentaDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.nombre").value("El nombre no puede estar vacío."));
        }
    }

    @Nested
    public class editarMethods{
        @Test
        void testEditar_successfully() throws Exception{
            Long id = 1L;
            Cuenta cuenta = new Cuenta(1L, "Cuenta Editar", new BigDecimal("20000.00"));
            Cuenta cuentaEditada = new Cuenta(1L, "Cuenta Nueva", new BigDecimal("50000.00"));
            CuentaDto cuentaDto = new CuentaDto(null, "Cuenta Editar", new BigDecimal("20000.00"));

            when(service.buscar(id)).thenReturn(cuenta);
            when(service.editar(any(Cuenta.class))).thenReturn(cuentaEditada);

            mockMvc.perform(put("/api/banco/cuenta/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cuenta)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombre").value("Cuenta Nueva"))
                    .andExpect(jsonPath("$.saldo").value(50000.00));
        }

        @Test
        void testEditar_notFound_Exception() throws Exception {
            CuentaDto cuentaDto = new CuentaDto(1L, "Cuenta Dto", new BigDecimal("20000.00"));
            when(service.buscar(any())).thenThrow(new RecursoNoEncontradoException("Cuenta con ID " + cuentaDto.getId() + " no encontrada."));

            mockMvc.perform(put("/api/banco/cuenta/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cuentaDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Cuenta con ID " + cuentaDto.getId() + " no encontrada."));
        }
    }

    @Nested
    public class buscarMethods{
        @Test
        void testBuscar_successfully() throws Exception{
            Cuenta cuenta = new Cuenta(1L, "Juan Perez", new BigDecimal("20000.00"));
            when(service.buscar(any())).thenReturn(cuenta);

            mockMvc.perform(get("/api/banco/cuenta/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                    .andExpect(jsonPath("$.saldo").value(20000.00));
        }

        @Test
        void testBuscar_notFound_Exception() throws Exception{
            Long id = 1L;
            when(service.buscar(any())).thenThrow(new RecursoNoEncontradoException("Cuenta con ID " + id + " no encontrada."));

            mockMvc.perform(get("/api/banco/cuenta/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Cuenta con ID " + id + " no encontrada."));
        }
    }

    @Nested
    public class eliminarMethods{
        @Test
        void testEliminar_successfully() throws Exception{
            Long id = 1L;

            mockMvc.perform(delete("/api/banco/cuenta/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void testEliminar_notFound_Exception() throws Exception{
            Long id = 1L;
            doThrow(new RecursoNoEncontradoException("Cuenta con ID " + id + " no encontrada.")).when(service).eliminar(id);
            mockMvc.perform(delete("/api/banco/cuenta/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Cuenta con ID " + id + " no encontrada."));
        }
    }

    @Nested
    public class buscarPorNombreMethods{
        @Test
        void testBuscarPorNombre_successfully() throws Exception {
            Cuenta cuenta = new Cuenta(1L, "Juan Perez", new BigDecimal("20000.00"));
            when(service.buscarPorNombre(cuenta.getNombre())).thenReturn(cuenta);

            mockMvc.perform(get("/api/banco/cuenta/nombre/Juan Perez"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                    .andExpect(jsonPath("$.saldo").value(20000.00));

        }

        @Test
        void testBuscarPorNombre_notFound_Exception() throws Exception {
            String nombre = "Juan Perez";
            when(service.buscarPorNombre(any(String.class))).thenThrow(new RecursoNoEncontradoException("Cuenta con nombre " + nombre + " no encontrada."));

            mockMvc.perform(get("/api/banco/cuenta/nombre/Juan%20Perez"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Cuenta con nombre " + nombre + " no encontrada."));
        }
    }
}