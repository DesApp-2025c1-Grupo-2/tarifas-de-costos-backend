package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.TipoCargaEspecifica;
import com.acme.tarifas.gestion.service.TipoCargaEspecificaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-carga-especifica")
public class TipoCargaEspecificaController {

    @Autowired
    TipoCargaEspecificaService tipoCargaEspecificaService;

    @GetMapping
    public List<TipoCargaEspecifica> obtenerTodos() {
        return tipoCargaEspecificaService.obtenerTodosTiposCargaEspecifica();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoCargaEspecifica> obtenerPorId(@PathVariable Long id) {
        return tipoCargaEspecificaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    };

    @PostMapping
    public ResponseEntity<TipoCargaEspecifica> crearTipoCargaEspecifica(@RequestBody TipoCargaEspecifica tipo) {
        TipoCargaEspecifica nuevo = tipoCargaEspecificaService.guardarTipoCargaEspecifica(tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTipoCargaEspecifica(@PathVariable Long id) {
        try {
            tipoCargaEspecificaService.eliminarTipoCargaEspecifica(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
