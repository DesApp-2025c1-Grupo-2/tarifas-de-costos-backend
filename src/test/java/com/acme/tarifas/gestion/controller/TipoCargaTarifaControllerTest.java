package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.TipoCargaTarifa;
import com.acme.tarifas.gestion.service.TipoCargaTarifaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TipoCargaTarifaController.class)
class TipoCargaTarifaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TipoCargaTarifaService tipoCargaTarifaService;

    @Autowired
    private ObjectMapper objectMapper;

    private TipoCargaTarifa tipo1;
    private TipoCargaTarifa tipo2;

    @BeforeEach
    void configurar() {
        tipo1 = new TipoCargaTarifa(1L, "Carga General", "Desc 1", true);
        tipo2 = new TipoCargaTarifa(2L, "Carga Refrigerada", "Desc 2", true);
    }

    // --- GET /{id} ---
    @Test
    @DisplayName("GET /{id} - Debe devolver el tipo por ID")
    void cuandoObtenerTipoCargaPorId_conIdValido_debeDevolverOk() throws Exception {
        when(tipoCargaTarifaService.obtenerPorId(1L)).thenReturn(Optional.of(tipo1));

        mockMvc.perform(get("/api/tipo-carga-tarifa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Carga General")));
    }

    @Test
    @DisplayName("GET /{id} - Debe devolver 404 si ID no existe")
    void cuandoObtenerTipoCargaPorId_conIdInvalido_debeDevolverNotFound() throws Exception {
        when(tipoCargaTarifaService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tipo-carga-tarifa/99"))
                .andExpect(status().isNotFound());
    }

    // --- GET / ---
    @Test
    @DisplayName("GET / - Debe devolver todos los tipos")
    void cuandoObtenerTodos_debeDevolverLista() throws Exception {
        when(tipoCargaTarifaService.obtenerTodosTiposCargaTarifa()).thenReturn(List.of(tipo1, tipo2));

        mockMvc.perform(get("/api/tipo-carga-tarifa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // --- DELETE /{id} ---
    @Test
    @DisplayName("DELETE /{id} - Debe eliminar si ID existe")
    void cuandoEliminarTipoCargaTarifa_conIdValido_debeDevolverOk() throws Exception {
        doNothing().when(tipoCargaTarifaService).eliminarTipoCargaTarifa(1L);

        mockMvc.perform(delete("/api/tipo-carga-tarifa/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /{id} - Debe devolver 404 si ID no existe")
    void cuandoEliminarTipoCargaTarifa_conIdInvalido_debeDevolverNotFound() throws Exception {
        doThrow(new Exception("Tipo de carga no encontrado")).when(tipoCargaTarifaService).eliminarTipoCargaTarifa(99L);

        mockMvc.perform(delete("/api/tipo-carga-tarifa/99"))
                .andExpect(status().isNotFound());
    }

    // --- POST / ---
    @Test
    @DisplayName("POST / - Debe crear el tipo y devolver 201")
    void cuandoCrearTipoCargaTarifa_conDatosValidos_debeDevolverCreated() throws Exception {
        when(tipoCargaTarifaService.guardarTipoCargaTarifa(any(TipoCargaTarifa.class))).thenReturn(tipo1);

        mockMvc.perform(post("/api/tipo-carga-tarifa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tipo1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("POST / - Debe devolver 400 si nombre está duplicado")
    void cuandoCrearTipoCargaTarifa_conNombreDuplicado_debeDevolverBadRequest() throws Exception {
        String errorMsg = "Ya existe un tipo de carga activo con ese nombre";
        when(tipoCargaTarifaService.guardarTipoCargaTarifa(any(TipoCargaTarifa.class)))
                .thenThrow(new IllegalArgumentException(errorMsg));

        mockMvc.perform(post("/api/tipo-carga-tarifa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tipo1)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString(errorMsg)));
    }

    // --- PUT /{id}/baja ---
    @Test
    @DisplayName("PUT /{id}/baja - Debe dar de baja y devolver OK")
    void cuandoBaja_conIdValido_debeDevolverOk() throws Exception {
        tipo1.setActivo(false);
        when(tipoCargaTarifaService.baja(1L)).thenReturn(tipo1);

        mockMvc.perform(put("/api/tipo-carga-tarifa/1/baja"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo", is(false)));
    }

    @Test
    @DisplayName("PUT /{id}/baja - Debe devolver 400 si no se encuentra")
    void cuandoBaja_conIdInvalido_debeDevolverBadRequest() throws Exception {
        String errorMsg = "tipo de carga no encontrada";
        when(tipoCargaTarifaService.baja(99L)).thenThrow(new Exception(errorMsg));

        mockMvc.perform(put("/api/tipo-carga-tarifa/99/baja"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString(errorMsg)));
    }

    @Test
    @DisplayName("PUT /{id}/baja - Debe devolver 400 si ya está inactivo")
    void cuandoBaja_yYaEstaInactivo_debeDevolverBadRequest() throws Exception {
        String errorMsg = "El tipo de carga ya está inactivo";
        when(tipoCargaTarifaService.baja(1L)).thenThrow(new Exception(errorMsg));

        mockMvc.perform(put("/api/tipo-carga-tarifa/1/baja"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString(errorMsg)));
    }

    // --- PUT /{id} ---
    @Test
    @DisplayName("PUT /{id} - Debe actualizar y devolver OK")
    void cuandoActualizarTipo_conDatosValidos_debeDevolverOk() throws Exception {
        TipoCargaTarifa tipoActualizado = new TipoCargaTarifa(1L, "Nombre Nuevo", "Desc Nueva", true);
        when(tipoCargaTarifaService.actualizarTipo(eq(1L), any(TipoCargaTarifa.class)))
                .thenReturn(Optional.of(tipoActualizado));

        mockMvc.perform(put("/api/tipo-carga-tarifa/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tipoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Nombre Nuevo")));
    }

    @Test
    @DisplayName("PUT /{id} - Debe devolver 404 si ID no existe")
    void cuandoActualizarTipo_conIdInvalido_debeDevolverNotFound() throws Exception {
        when(tipoCargaTarifaService.actualizarTipo(eq(99L), any(TipoCargaTarifa.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/tipo-carga-tarifa/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tipo1)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /{id} - Debe devolver 400 si nombre está duplicado")
    void cuandoActualizarTipo_conNombreDuplicado_debeDevolverBadRequest() throws Exception {
        String errorMsg = "Ya existe otro tipo de carga activo con ese nombre";
        when(tipoCargaTarifaService.actualizarTipo(eq(1L), any(TipoCargaTarifa.class)))
                .thenThrow(new IllegalArgumentException(errorMsg));

        mockMvc.perform(put("/api/tipo-carga-tarifa/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tipo1)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString(errorMsg)));
    }
}