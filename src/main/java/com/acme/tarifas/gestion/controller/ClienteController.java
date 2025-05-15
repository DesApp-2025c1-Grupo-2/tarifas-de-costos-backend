package com.acme.tarifas.gestion.controller;

import com.acme.tarifas.gestion.entity.Cliente;
import com.acme.tarifas.gestion.entity.Viaje;
import com.acme.tarifas.gestion.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        Cliente nuevo = clienteService.guardarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping
    public List<Cliente> obtenerTodosClientes() {
        return clienteService.obtenerTodosClientes();
    }

    @GetMapping("/{id}/viajes")
    public List<Viaje> obtenerViajesPorCliente(@PathVariable Long id) {
        return clienteService.obtenerViajesCliente(id);
    }
}