package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.Adicional;
import com.acme.tarifas.gestion.service.AdicionalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdicionalController.class)
class AdicionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdicionalService adicionalService;

    @Autowired
    private ObjectMapper objectMapper;

    private Adicional adicional1;
    private Adicional adicional2;

    @BeforeEach
    void configurar() {
        adicional1 = new Adicional(1L, "Servicio Express", 150.0, "Envío rápido", true, true);
        adicional2 = new Adicional(2L, "Garantía Extendida", 80.0, "Cobertura extra", true, false);
    }

    // --- Test para POST /api/adicionales ---

    @Test
    @DisplayName("POST /api/adicionales - Debe crear un nuevo adicional")
    void cuandoCrearAdicional_conDatosValidos_debeDevolverCreated() throws Exception {
        when(adicionalService.guardarAdicional(any(Adicional.class))).thenReturn(adicional1);
        mockMvc.perform(post("/api/adicionales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adicional1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Servicio Express")));
    }

    @Test
    @DisplayName("POST /api/adicionales - Debe fallar si el nombre ya existe")
    void cuandoCrearAdicional_yNombreExiste_debeDevolverBadRequest() throws Exception {
        // El servicio lanza la excepción
        when(adicionalService.guardarAdicional(any(Adicional.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un adicional con ese nombre"));

        // Ahora se espera un 400
        mockMvc.perform(post("/api/adicionales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adicional1)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Ya existe un adicional con ese nombre")));
    }

    // --- Test para GET /api/adicionales ---

    @Test
    @DisplayName("GET /api/adicionales - Debe devolver lista de adicionales")
    void cuandoObtenerTodosAdicionales_debeDevolverListaDeAdicionales() throws Exception {
        List<Adicional> lista = Arrays.asList(adicional1, adicional2);
        when(adicionalService.obtenerTodos()).thenReturn(lista);

        mockMvc.perform(get("/api/adicionales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Servicio Express")))
                .andExpect(jsonPath("$[1].nombre", is("Garantía Extendida")));
    }

    // --- Test para GET /api/adicionales/{id} ---

    @Test
    @DisplayName("GET /api/adicionales/{id} - Debe devolver un adicional por ID")
    void cuandoObtenerAdicionalPorId_conIdValido_debeDevolverAdicional() throws Exception {
        when(adicionalService.obtenerPorId(1L)).thenReturn(Optional.of(adicional1));

        mockMvc.perform(get("/api/adicionales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Servicio Express")));
    }

    @Test
    @DisplayName("GET /api/adicionales/{id} - Debe devolver 404 si el ID no existe")
    void cuandoObtenerAdicionalPorId_conIdInvalido_debeDevolverNotFound() throws Exception {
        when(adicionalService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/adicionales/99"))
                .andExpect(status().isNotFound());
    }

    // --- Test para PUT /api/adicionales/{id}/costo-default ---

    @Test
    @DisplayName("PUT /{id}/costo-default - Debe actualizar el costo y devolver OK")
    void cuandoActualizarCostoDefault_conIdValido_debeDevolverOk() throws Exception {
        Adicional actualizado = new Adicional(1L, "Servicio Express", 200.0, "Envío rápido", true, true);
        when(adicionalService.actualizarCostoDefault(1L, 200.0)).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/adicionales/1/costo-default")
                        .param("nuevoCosto", "200.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costoDefault", is(200.0)));
    }

    @Test
    @DisplayName("PUT /{id}/costo-default - Debe devolver 404 si el ID no existe")
    void cuandoActualizarCostoDefault_conIdInvalido_debeDevolverNotFound() throws Exception {
        when(adicionalService.actualizarCostoDefault(99L, 200.0)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/adicionales/99/costo-default")
                        .param("nuevoCosto", "200.0"))
                .andExpect(status().isNotFound());
    }

    // --- Test para DELETE /api/adicionales/{id} ---

    @Test
    @DisplayName("DELETE /{id} - Debe eliminar el adicional y devolver 200 OK")
    void cuandoEliminarAdicional_conIdValido_debeDevolverOk() throws Exception {
        when(adicionalService.eliminarAdicional(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/adicionales/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /{id} - Debe devolver 404 si el ID no existe")
    void cuandoEliminarAdicional_conIdInvalido_debeDevolverNotFound() throws Exception {
        when(adicionalService.eliminarAdicional(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/adicionales/99"))
                .andExpect(status().isNotFound());
    }

    // --- Test para PUT /api/adicionales/{id}/baja ---

    @Test
    @DisplayName("PUT /{id}/baja - Debe dar de baja y devolver 200 OK")
    void cuandoBaja_conIdValido_debeDevolverOk() throws Exception {
        adicional1.setActivo(false); // Simulamos el resultado
        when(adicionalService.baja(1L)).thenReturn(adicional1);

        mockMvc.perform(put("/api/adicionales/1/baja"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo", is(false)));
    }

    @Test
    @DisplayName("PUT /{id}/baja - Debe devolver 400 si no se encuentra")
    void cuandoBaja_conIdInvalido_debeDevolverBadRequest() throws Exception {
        // El servicio lanza la excepción
        when(adicionalService.baja(99L)).thenThrow(new Exception("Adicional no encontrado"));

        // El controlador la captura y devuelve 400
        mockMvc.perform(put("/api/adicionales/99/baja"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Adicional no encontrado")));
    }

    @Test
    @DisplayName("PUT /{id}/baja - Debe devolver 400 si ya está inactivo")
    void cuandoBaja_yYaEstaInactivo_debeDevolverBadRequest() throws Exception {
        when(adicionalService.baja(1L)).thenThrow(new Exception("El adicional ya está inactivo"));

        mockMvc.perform(put("/api/adicionales/1/baja"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("El adicional ya está inactivo")));
    }

    // --- Test para PUT /api/adicionales/{id} ---

    @Test
    @DisplayName("PUT /{id} - Debe actualizar el adicional y devolver 200 OK")
    void cuandoActualizarAdicional_conDatosValidos_debeDevolverOk() throws Exception {
        Adicional datosNuevos = new Adicional(1L, "Nombre Nuevo", 200.0, "Desc Nueva", false, false);
        when(adicionalService.actualizarAdicional(eq(1L), any(Adicional.class))).thenReturn(Optional.of(datosNuevos));

        mockMvc.perform(put("/api/adicionales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datosNuevos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Nombre Nuevo")))
                .andExpect(jsonPath("$.costoDefault", is(200.0)));
    }

    @Test
    @DisplayName("PUT /{id} - Debe devolver 404 si el ID no existe")
    void cuandoActualizarAdicional_conIdInvalido_debeDevolverNotFound() throws Exception {
        Adicional datosNuevos = new Adicional(99L, "Nombre Nuevo", 200.0, "Desc Nueva", false, false);
        when(adicionalService.actualizarAdicional(eq(99L), any(Adicional.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/adicionales/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datosNuevos)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /{id} - Debe fallar si el nombre duplicado existe")
    void cuandoActualizarAdicional_yNombreExiste_debeDevolverBadRequest() throws Exception {
        Adicional datosNuevos = new Adicional(1L, "Nombre Duplicado", 200.0, null, true, true);

        // El servicio lanza la excepción
        when(adicionalService.actualizarAdicional(eq(1L), any(Adicional.class)))
                .thenThrow(new IllegalArgumentException("Ya existe otro adicional activo con ese nombre"));

        // Ahora esperamos un 400
        mockMvc.perform(put("/api/adicionales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datosNuevos)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Ya existe otro adicional activo con ese nombre")));
    }
}