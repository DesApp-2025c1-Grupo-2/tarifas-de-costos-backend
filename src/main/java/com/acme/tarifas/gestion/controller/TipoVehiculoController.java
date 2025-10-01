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

    // --- INICIO DE LA MODIFICACIÓN ---
    @GetMapping
    public List<TipoVehiculoViewDTO> obtenerTodosTipos() {
        // Se vuelve a consultar la lista de vehículos para obtener todos los datos
        // (peso, volumen),
        // pero se mantiene el filtro para devolver solo tipos de vehículo únicos.
        return viajesClient.getVehiculos().stream()
                .filter(Objects::nonNull)
                .filter(v -> v.getTipo() != null && v.getTipo().getId() != null)
                .map(TipoVehiculoViewDTO::new)
                .filter(distinctByKey(TipoVehiculoViewDTO::getId))
                .collect(Collectors.toList());
    }

    // Función de utilidad para filtrar elementos duplicados por una propiedad (el
    // ID)
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    // --- FIN DE LA MODIFICACIÓN ---

    @GetMapping("/form")
    public List<TipoVehiculoFormDTO> obtenerParaFormulario() {
        return viajesClient.getTiposVehiculo().stream()
                .map(TipoVehiculoFormDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TipoVehiculoDTO getTipoPorId(@PathVariable String id) {
        return viajesClient.getTiposVehiculoById(id);
    }
}