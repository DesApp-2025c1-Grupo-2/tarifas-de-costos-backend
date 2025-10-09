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

    @GetMapping
    public List<TipoVehiculoViewDTO> obtenerTodosTipos() {
        return viajesClient.getVehiculos().stream()
                .filter(v -> v != null && v.getDeletedAt() == null && v.getTipo() != null
                        && v.getTipo().getId() != null)
                .map(vehiculo -> new TipoVehiculoViewDTO(vehiculo, vehiculo.getTipo()))
                .filter(distinctByKey(TipoVehiculoViewDTO::getId))
                .collect(Collectors.toList());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

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