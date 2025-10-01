package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.clients.ViajesClient;
import com.acme.tarifas.gestion.dto.TipoVehiculoDTO;
import com.acme.tarifas.gestion.dto.TipoVehiculoFormDTO;
import com.acme.tarifas.gestion.dto.TipoVehiculoViewDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipos-vehiculo")
public class TipoVehiculoController {

    private final ViajesClient viajesClient;

    public TipoVehiculoController(ViajesClient viajesClient) {
        this.viajesClient = viajesClient;
    }

    @GetMapping("/form")
    public List<TipoVehiculoFormDTO> obtenerParaFormulario() {
        return viajesClient.getTiposVehiculo().stream()
                .map(TipoVehiculoFormDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<TipoVehiculoViewDTO> obtenerTodosTipos() {
        // --- INICIO DE LA MODIFICACIÓN ---
        // Se procesa la lista de vehículos para devolver solo tipos de vehículo únicos.
        List<TipoVehiculoViewDTO> tiposNoUnicos = viajesClient.getVehiculos().stream()
                .filter(Objects::nonNull) // 1. Filtra vehículos nulos de la API
                .filter(v -> v.getTipo() != null && v.getTipo().getId() != null) // 2. Asegura que el vehículo tenga un
                                                                                 // tipo con ID
                .map(TipoVehiculoViewDTO::new)
                .collect(Collectors.toList());

        // 3. Filtra la lista para que solo contenga elementos con un ID único
        return tiposNoUnicos.stream()
                .filter(distinctByKey(TipoVehiculoViewDTO::getId))
                .collect(Collectors.toList());
        // --- FIN DE LA MODIFICACIÓN ---
    }

    // Función de utilidad para filtrar elementos duplicados por una propiedad (el
    // ID)
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @GetMapping("/{id}")
    public TipoVehiculoDTO getTipoPorId(@PathVariable String id) {
        return viajesClient.getTiposVehiculoById(id);
    }
}