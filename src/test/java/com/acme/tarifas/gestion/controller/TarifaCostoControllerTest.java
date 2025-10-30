package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.dto.TarifaCostoDTO;
import com.acme.tarifas.gestion.entity.Adicional;
import com.acme.tarifas.gestion.entity.TarifaAdicional;
import com.acme.tarifas.gestion.entity.TarifaCosto;
import com.acme.tarifas.gestion.service.TarifaCostoService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarifaCostoController.class)
class TarifaCostoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TarifaCostoService tarifaService;

        @Autowired
        private ObjectMapper objectMapper;

        private TarifaCosto tarifaEntidad;
        private TarifaCostoDTO tarifaDTO;
        private TarifaAdicional tarifaAdicional;
        private Adicional adicional;

        @BeforeEach
        void configurar() {
                objectMapper.registerModule(new JavaTimeModule());

                // Entidad Adicional base
                adicional = new Adicional(1L, "Seguro", 100.0, "Seguro de carga", true, true);

                // Entidad TarifaAdicional
                tarifaAdicional = new TarifaAdicional();
                tarifaAdicional.setId(10L);
                tarifaAdicional.setAdicional(adicional);
                tarifaAdicional.setCostoEspecifico(150.0);

                // Entidad TarifaCosto (para enviar en POST/PUT)
                tarifaEntidad = new TarifaCosto();
                tarifaEntidad.setId(1L);
                tarifaEntidad.setNombreTarifa("Tarifa Patagónica");
                tarifaEntidad.setValorBase(5000.0);
                tarifaEntidad.setTransportistaId("TRANSPORT-ID-1");
                tarifaEntidad.setTipoVehiculoId("VEHICULO-ID-1");
                tarifaEntidad.setAdicionales(List.of(tarifaAdicional));
                tarifaEntidad.setFechaCreacion(LocalDateTime.now());

                // DTO TarifaCosto (para recibir en GET)
                tarifaDTO = new TarifaCostoDTO();
                tarifaDTO.setId(1L);
                tarifaDTO.setNombreTarifa("Tarifa Patagónica");
                tarifaDTO.setTransportistaNombre("Transporte Sur S.A.");
                tarifaDTO.setTipoVehiculoNombre("Camión Grande");
                tarifaDTO.setValorBase(5000.0);
                tarifaDTO.setTotal(5150.0);
                tarifaDTO.setAdicionales(List.of(tarifaAdicional));
        }

        // --- POST /api/tarifas ---

        @Test
        @DisplayName("POST / - Debe crear tarifa y devolver DTO")
        void cuandoCrearTarifa_conDatosValidos_debeDevolverCreated() throws Exception {
                when(tarifaService.crearTarifa(any(TarifaCosto.class))).thenReturn(tarifaDTO);

                mockMvc.perform(post("/api/tarifas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarifaEntidad)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.nombreTarifa", is("Tarifa Patagónica")))
                                .andExpect(jsonPath("$.transportistaNombre", is("Transporte Sur S.A.")));
        }

        @Test
        @DisplayName("POST / - Debe devolver 400 si la validación del servicio falla")
        void cuandoCrearTarifa_conDatosInvalidos_debeDevolverBadRequest() throws Exception {
                String errorMsg = "El valor base debe ser mayor que cero y obligatorio.";
                when(tarifaService.crearTarifa(any(TarifaCosto.class)))
                                .thenThrow(new IllegalArgumentException(errorMsg));

                mockMvc.perform(post("/api/tarifas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarifaEntidad)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.mensaje", is(errorMsg)));
        }

        // --- PUT /api/tarifas/{id} ---

        @Test
        @DisplayName("PUT /{id} - Debe actualizar tarifa y devolver OK")
        void cuandoActualizarTarifa_conIdValido_debeDevolverOk() throws Exception {
                when(tarifaService.actualizarTarifa(eq(1L), any(TarifaCosto.class)))
                                .thenReturn(Optional.of(tarifaEntidad));

                mockMvc.perform(put("/api/tarifas/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarifaEntidad)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        @DisplayName("PUT /{id} - Debe devolver 404 si ID no existe")
        void cuandoActualizarTarifa_conIdInvalido_debeDevolverNotFound() throws Exception {
                when(tarifaService.actualizarTarifa(eq(99L), any(TarifaCosto.class)))
                                .thenReturn(Optional.empty());

                mockMvc.perform(put("/api/tarifas/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarifaEntidad)))
                                .andExpect(status().isNotFound());
        }

        // --- GET /api/tarifas ---

        @Test
        @DisplayName("GET / - Debe devolver todas las tarifas (DTOs)")
        void cuandoObtenerTodasTarifas_sinFiltros_debeDevolverListaDTO() throws Exception {
                when(tarifaService.filtrarTarifas(null, null, null, null, null))
                                .thenReturn(List.of(tarifaDTO));

                mockMvc.perform(get("/api/tarifas"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].transportistaNombre", is("Transporte Sur S.A.")));
        }

        // --- GET /api/tarifas/{id} ---

        @Test
        @DisplayName("GET /{id} - Debe devolver DTO por ID")
        void cuandoGetTarifaPorId_conIdValido_debeDevolverDTO() throws Exception {
                when(tarifaService.obtenerTarifaPorId(1L)).thenReturn(Optional.of(tarifaDTO));

                mockMvc.perform(get("/api/tarifas/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        @DisplayName("GET /{id} - Debe devolver 404 si ID no existe")
        void cuandoGetTarifaPorId_conIdInvalido_debeDevolverNotFound() throws Exception {
                when(tarifaService.obtenerTarifaPorId(99L)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/tarifas/99"))
                                .andExpect(status().isNotFound());
        }

        // --- POST /api/tarifas/{id}/adicionales ---

        @Test
        @DisplayName("POST /{id}/adicionales - Debe agregar adicional y devolver OK")
        void cuandoAgregarAdicional_conDatosValidos_debeDevolverOk() throws Exception {
                when(tarifaService.agregarAdicional(eq(1L), any(TarifaAdicional.class)))
                                .thenReturn(Optional.of(tarifaAdicional));

                mockMvc.perform(post("/api/tarifas/1/adicionales")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarifaAdicional)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.costoEspecifico", is(150.0)));
        }

        @Test
        @DisplayName("POST /{id}/adicionales - Debe devolver 404 si Tarifa ID no existe")
        void cuandoAgregarAdicional_conTarifaIdInvalido_debeDevolverNotFound() throws Exception {
                when(tarifaService.agregarAdicional(eq(99L), any(TarifaAdicional.class)))
                                .thenReturn(Optional.empty());

                mockMvc.perform(post("/api/tarifas/99/adicionales")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarifaAdicional)))
                                .andExpect(status().isNotFound());
        }

        // --- PUT /api/tarifas/{id}/valor-base ---

        @Test
        @DisplayName("PUT /{id}/valor-base - Debe actualizar valor y devolver OK")
        void cuandoActualizarValorBase_conIdValido_debeDevolverOk() throws Exception {
                tarifaEntidad.setValorBase(999.0);
                when(tarifaService.actualizarValorBase(1L, 999.0))
                                .thenReturn(Optional.of(tarifaEntidad));

                mockMvc.perform(put("/api/tarifas/1/valor-base")
                                .param("nuevoValor", "999.0"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.valorBase", is(999.0)));
        }

        @Test
        @DisplayName("PUT /{id}/valor-base - Debe devolver 404 si ID no existe")
        void cuandoActualizarValorBase_conIdInvalido_debeDevolverNotFound() throws Exception {
                when(tarifaService.actualizarValorBase(99L, 999.0))
                                .thenReturn(Optional.empty());

                mockMvc.perform(put("/api/tarifas/99/valor-base")
                                .param("nuevoValor", "999.0"))
                                .andExpect(status().isNotFound());
        }

        // --- GET /api/tarifas/{id}/adicionales ---

        @Test
        @DisplayName("GET /{id}/adicionales - Debe devolver lista de adicionales")
        void cuandoGetAdicionalesPorTarifa_conIdValido_debeDevolverLista() throws Exception {
                when(tarifaService.obtenerAdicionalesPorTarifa(1L))
                                .thenReturn(List.of(tarifaAdicional));

                mockMvc.perform(get("/api/tarifas/1/adicionales"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("GET /{id}/adicionales - Debe devolver 404 si no hay adicionales")
        void cuandoGetAdicionalesPorTarifa_conListaVacia_debeDevolverNotFound() throws Exception {
                when(tarifaService.obtenerAdicionalesPorTarifa(1L))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/tarifas/1/adicionales"))
                                .andExpect(status().isNotFound());
        }

        // --- PUT /api/tarifas/{id}/baja ---

        @Test
        @DisplayName("PUT /{id}/baja - Debe dar de baja y devolver 204")
        void cuandoCambiarVigencia_debeDevolverNoContent() throws Exception {
                doNothing().when(tarifaService).cambiarVigencia(1L);

                mockMvc.perform(put("/api/tarifas/1/baja"))
                                .andExpect(status().isNoContent());
        }
}