package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.CargaDeCombustible;
import com.acme.tarifas.gestion.service.CargaDeCombustibleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CargaDeCombustibleController.class)
class CargaDeCombustibleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CargaDeCombustibleService cargaService;

    @Autowired
    private ObjectMapper objectMapper;

    private CargaDeCombustible carga1;
    private CargaDeCombustible carga2;
    private LocalDateTime fechaTest;

    @BeforeEach
    void configurar() {
        objectMapper.registerModule(new JavaTimeModule());
        fechaTest = LocalDateTime.of(2025, 10, 30, 10, 0, 0);
        carga1 = new CargaDeCombustible(1L, "AA123BB", fechaTest, 50.5, "TICKET-001", 5050.0, true);
        carga2 = new CargaDeCombustible(2L, "CC456DD", fechaTest.minusDays(1), 30.0, "TICKET-002", 3000.0, true);
    }

    @Test
    @DisplayName("GET / - Debe devolver todas las cargas vigentes")
    void cuandoObtenerTodasLasCargas_debeDevolverLista() throws Exception {
        when(cargaService.obtenerTodasLasCargas(null)).thenReturn(List.of(carga1, carga2));

        mockMvc.perform(get("/api/cargasDeCombustible"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].vehiculoId", is("AA123BB")));
    }

    @Test
    @DisplayName("GET / - Debe devolver lista vacía si no hay cargas")
    void cuandoObtenerTodasLasCargas_yNoHayCargas_debeDevolverListaVacia() throws Exception {
        when(cargaService.obtenerTodasLasCargas(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cargasDeCombustible"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST / - Debe crear una carga con datos válidos")
    void cuandoCrearCarga_conDatosValidos_debeDevolverCreated() throws Exception {
        when(cargaService.guardarCarga(any(CargaDeCombustible.class))).thenReturn(carga1);

        mockMvc.perform(post("/api/cargasDeCombustible")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carga1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("PUT /{id} - Debe actualizar si ID y datos son válidos")
    void cuandoActualizarCarga_conIdValido_debeDevolverOk() throws Exception {
        CargaDeCombustible cargaActualizada = new CargaDeCombustible(1L, "NUEVO-ID", fechaTest, 60.0, "TICKET-999",
                6000.0, true);
        when(cargaService.actualizarCarga(eq(1L), any(CargaDeCombustible.class)))
                .thenReturn(Optional.of(cargaActualizada));

        mockMvc.perform(put("/api/cargasDeCombustible/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cargaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehiculoId", is("NUEVO-ID")))
                .andExpect(jsonPath("$.litrosCargados", is(60.0)));
    }

    @Test
    @DisplayName("PUT /{id} - Debe devolver 404 si ID no existe")
    void cuandoActualizarCarga_conIdInvalido_debeDevolverNotFound() throws Exception {
        when(cargaService.actualizarCarga(eq(99L), any(CargaDeCombustible.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/cargasDeCombustible/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carga1)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /{id}/baja - Debe dar de baja y devolver 204")
    void cuandoDarBajaCarga_debeDevolverNoContent() throws Exception {
        doNothing().when(cargaService).darBaja(1L);

        mockMvc.perform(put("/api/cargasDeCombustible/1/baja"))
                .andExpect(status().isNoContent());

        verify(cargaService).darBaja(1L);
    }

    @Test
    @DisplayName("POST / - Debe devolver 400 si los litros son negativos")
    void cuandoCrearCarga_conLitrosInvalidos_debeDevolverBadRequest() throws Exception {
        CargaDeCombustible cargaInvalida = new CargaDeCombustible(null, "AA123BB", fechaTest, -10.0, "TICKET-001",
                5000.0, true);

        mockMvc.perform(post("/api/cargasDeCombustible")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cargaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                    String exceptionMessage = result.getResolvedException().getMessage();
                    assertThat(exceptionMessage, containsString("Los litros deben ser un valor positivo"));
                });
    }

    @Test
    @DisplayName("POST / - Debe devolver 400 si el ticket está en blanco")
    void cuandoCrearCarga_conTicketInvalido_debeDevolverBadRequest() throws Exception {
        CargaDeCombustible cargaInvalida = new CargaDeCombustible(null, "AA123BB", fechaTest, 50.0, "", 5000.0, true);

        mockMvc.perform(post("/api/cargasDeCombustible")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cargaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                    String exceptionMessage = result.getResolvedException().getMessage();
                    assertThat(exceptionMessage, containsString("El número de ticket es obligatorio"));
                });
    }

    @Test
    @DisplayName("PUT /{id} - Debe devolver 400 si datos son inválidos")
    void cuandoActualizarCarga_conDatosInvalidos_debeDevolverBadRequest() throws Exception {
        CargaDeCombustible cargaInvalida = new CargaDeCombustible(1L, "AA123BB", fechaTest, -50.0, "TICKET-001",
                -5000.0, true);

        mockMvc.perform(put("/api/cargasDeCombustible/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cargaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                    String exceptionMessage = result.getResolvedException().getMessage();
                    assertThat(exceptionMessage, containsString("Los litros deben ser un valor positivo"));
                    assertThat(exceptionMessage, containsString("El precio total debe ser un valor positivo"));
                });
    }
}