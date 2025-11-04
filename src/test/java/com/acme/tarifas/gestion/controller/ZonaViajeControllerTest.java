package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.ZonaViajeDTO;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.service.ZonaViajeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ZonaViajeController.class)
class ZonaViajeControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ZonaViajeService zonaService;

        @Autowired
        private ObjectMapper objectMapper;

        private ZonaViajeDTO dto1;
        private ZonaViajeDTO dto2;

        @BeforeEach
        void configurar() {
                dto1 = new ZonaViajeDTO();
                dto1.setId(1L);
                dto1.setNombre("Zona Norte");
                dto1.setDescripcion("Zona Norte Desc");
                dto1.setActivo(true);
                dto1.setProvinciasNombres(Set.of("Buenos Aires", "Santa Fe"));

                dto2 = new ZonaViajeDTO();
                dto2.setId(2L);
                dto2.setNombre("Zona Sur");
                dto2.setActivo(true);
                dto2.setProvinciasNombres(Set.of("Chubut"));
        }

        // --- GET /api/zonas ---

        @Test
        @DisplayName("GET /api/zonas - Debe devolver lista de DTOs")
        void cuandoObtenerTodasLasZonas_debeDevolverListaDTOs() throws Exception {
                when(zonaService.getZonasDTO()).thenReturn(List.of(dto1, dto2));

                mockMvc.perform(get("/api/zonas"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].nombre", is("Zona Norte")));
        }

        // --- GET /api/zonas/{id} ---

        @Test
        @DisplayName("GET /api/zonas/{id} - Debe devolver DTO por ID")
        void cuandoObtenerZonaPorId_conIdValido_debeDevolverDTO() throws Exception {
                when(zonaService.getZonaDTOById(1L)).thenReturn(dto1);

                mockMvc.perform(get("/api/zonas/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.nombre", is("Zona Norte")));
        }

        @Test
        @DisplayName("GET /api/zonas/{id} - Debe devolver 404 si ID no existe")
        void cuandoObtenerZonaPorId_conIdInvalido_debeDevolverNotFound() throws Exception {
                when(zonaService.getZonaDTOById(99L)).thenThrow(new EntityNotFoundException("No existe"));

                mockMvc.perform(get("/api/zonas/99"))
                                .andExpect(status().isNotFound());
        }

        // --- GET /api/zonas?nombre=... ---

        @Test
        @DisplayName("GET /api/zonas?nombre= - Debe devolver DTO por nombre")
        void cuandoObtenerZonaPorNombre_conNombreValido_debeDevolverDTO() throws Exception {
                when(zonaService.getZonaDTOByNombre("Zona Norte")).thenReturn(Optional.of(dto1));

                mockMvc.perform(get("/api/zonas").param("nombre", "Zona Norte"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre", is("Zona Norte")));
        }

        @Test
        @DisplayName("GET /api/zonas?nombre= - Debe devolver 404 si nombre no existe")
        void cuandoObtenerZonaPorNombre_conNombreInvalido_debeDevolverNotFound() throws Exception {
                when(zonaService.getZonaDTOByNombre("Inexistente")).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/zonas").param("nombre", "Inexistente"))
                                .andExpect(status().isNotFound());
        }

        // --- POST /api/zonas ---

        @Test
        @DisplayName("POST /api/zonas - Debe crear zona y devolver DTO")
        void cuandoCrearZona_conDatosValidos_debeDevolverCreated() throws Exception {
                when(zonaService.guardarZonaYDevolverDTO(any(ZonaViajeDTO.class))).thenReturn(dto1);

                mockMvc.perform(post("/api/zonas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto1)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        @DisplayName("POST /api/zonas - Debe devolver 400 si nombre está duplicado")
        void cuandoCrearZona_conNombreDuplicado_debeDevolverBadRequest() throws Exception {
                String errorMsg = "Ya existe una zona activa con ese nombre";
                when(zonaService.guardarZonaYDevolverDTO(any(ZonaViajeDTO.class)))
                                .thenThrow(new IllegalArgumentException(errorMsg));

                mockMvc.perform(post("/api/zonas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto1)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error", is(errorMsg)));
        }

        // --- DELETE /api/zonas/{id} ---

        @Test
        @DisplayName("DELETE /api/zonas/{id} - Debe eliminar y devolver 200 OK")
        void cuandoEliminarZona_conIdValido_debeDevolverOk() throws Exception {
                doNothing().when(zonaService).eliminarZona(1L);

                mockMvc.perform(delete("/api/zonas/1"))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("DELETE /api/zonas/{id} - Debe devolver 404 si ID no existe")
        void cuandoEliminarZona_conIdInvalido_debeDevolverNotFound() throws Exception {
                doThrow(new Exception("No existe")).when(zonaService).eliminarZona(99L);

                mockMvc.perform(delete("/api/zonas/99"))
                                .andExpect(status().isNotFound());
        }

        // --- TEST ELIMINADO ---
        // @Test
        // @DisplayName("GET /comparativa-costos - Debe devolver el mapa de
        // comparativa")
        // void cuandoCompararCostosPorZona_debeDevolverMapa() throws Exception {
        // ...
        // }
        // --- FIN DE TEST ELIMINADO ---

        // --- GET /api/zonas/{id}/tarifas ---

        @Test
        @DisplayName("GET /{id}/tarifas - Debe devolver tarifas de la zona")
        void cuandoObtenerTarifasPorZona_conIdValido_debeDevolverTarifas() throws Exception {
                TarifaCosto tarifaMock = mock(TarifaCosto.class);
                when(tarifaMock.getId()).thenReturn(100L);

                when(zonaService.getZonaDTOById(1L)).thenReturn(dto1);
                when(zonaService.obtenerTarifasZona(1L)).thenReturn(List.of(tarifaMock));

                mockMvc.perform(get("/api/zonas/1/tarifas"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].id", is(100)));
        }

        @Test
        @DisplayName("GET /{id}/tarifas - Debe devolver 204 si no hay tarifas")
        void cuandoObtenerTarifasPorZona_sinTarifas_debeDevolverNoContent() throws Exception {
                when(zonaService.getZonaDTOById(1L)).thenReturn(dto1);
                when(zonaService.obtenerTarifasZona(1L)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/zonas/1/tarifas"))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("GET /{id}/tarifas - Debe devolver 404 si la zona no existe")
        void cuandoObtenerTarifasPorZona_conIdZonaInvalido_debeDevolverNotFound() throws Exception {
                when(zonaService.getZonaDTOById(99L)).thenThrow(new EntityNotFoundException("No existe"));

                mockMvc.perform(get("/api/zonas/99/tarifas"))
                                .andExpect(status().isNotFound());
        }

        // --- PUT /api/zonas/{id} ---

        @Test
        @DisplayName("PUT /api/zonas/{id} - Debe actualizar zona y devolver DTO")
        void cuandoActualizarZona_conDatosValidos_debeDevolverOk() throws Exception {
                ZonaViajeDTO dtoActualizado = new ZonaViajeDTO();
                dtoActualizado.setId(1L);
                dtoActualizado.setNombre("Zona Norte Actualizada");

                when(zonaService.actualizarZonaYDevolverDTO(eq(1L), any(ZonaViajeDTO.class)))
                                .thenReturn(Optional.of(dtoActualizado));

                mockMvc.perform(put("/api/zonas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto1))) // Enviamos dto1 como body
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre", is("Zona Norte Actualizada")));
        }

        @Test
        @DisplayName("PUT /api/zonas/{id} - Debe devolver 404 si ID no existe")
        void cuandoActualizarZona_conIdInvalido_debeDevolverNotFound() throws Exception {
                when(zonaService.actualizarZonaYDevolverDTO(eq(99L), any(ZonaViajeDTO.class)))
                                .thenReturn(Optional.empty());

                mockMvc.perform(put("/api/zonas/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto1)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("PUT /api/zonas/{id} - Debe devolver 400 si nombre está duplicado")
        void cuandoActualizarZona_conNombreDuplicado_debeDevolverBadRequest() throws Exception {
                String errorMsg = "Ya existe otra zona activa con ese nombre";
                when(zonaService.actualizarZonaYDevolverDTO(eq(1L), any(ZonaViajeDTO.class)))
                                .thenThrow(new IllegalArgumentException(errorMsg));

                mockMvc.perform(put("/api/zonas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto1)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error", is(errorMsg)));
        }

        // --- PUT /api/zonas/{id}/baja ---

        @Test
        @DisplayName("PUT /{id}/baja - Debe dar de baja y devolver DTO")
        void cuandoBaja_conIdValido_debeDevolverOk() throws Exception {
                dto1.setActivo(false);
                when(zonaService.bajaYDevolverDTO(1L)).thenReturn(dto1);

                mockMvc.perform(put("/api/zonas/1/baja"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.activo", is(false)));
        }

        @Test
        @DisplayName("PUT /{id}/baja - Debe devolver 400 si ID no existe")
        void cuandoBaja_conIdInvalido_debeDevolverBadRequest() throws Exception {
                String errorMsg = "Zona no encontrada";
                when(zonaService.bajaYDevolverDTO(99L)).thenThrow(new Exception(errorMsg));

                mockMvc.perform(put("/api/zonas/99/baja"))
                                .andExpect(status().isBadRequest())
                                .andExpect(status().reason(containsString(errorMsg)));
        }

        @Test
        @DisplayName("PUT /{id}/baja - Debe devolver 400 si ya está inactiva")
        void cuandoBaja_yYaEstaInactiva_debeDevolverBadRequest() throws Exception {
                String errorMsg = "La zona ya está inactiva";
                when(zonaService.bajaYDevolverDTO(1L)).thenThrow(new Exception(errorMsg));

                mockMvc.perform(put("/api/zonas/1/baja"))
                                .andExpect(status().isBadRequest())
                                .andExpect(status().reason(containsString(errorMsg)));
        }
}